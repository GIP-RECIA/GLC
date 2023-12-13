/*
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.glc.services.db;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.glc.configuration.Constants;
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.APersonneAStructure;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.APersonneAStructureRepository2;
import fr.recia.glc.db.repositories.education.DisciplineRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.models.mappers.AdditionalFonctionMapping;
import fr.recia.glc.models.mappers.AdditionalFonctionMappingFiliere;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.SANS_OBJET;

@Slf4j
@Service
public class FonctionService {

  @Autowired
  private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
  @Autowired
  private APersonneAStructureRepository2 aPersonneAStructureRepository2;
  @Autowired
  private APersonneRepository<APersonne> aPersonneRepository;
  @Autowired
  private AStructureRepository<AStructure> aStructureRepository;
  @Autowired
  private FonctionRepository<Fonction> fonctionRepository;
  @Autowired
  private DisciplineRepository<Discipline> disciplineRepository;
  @Autowired
  private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;

  @Autowired
  private PersonneService personneService;

  private static final String ALL = "ALL";
  public static final String FILIERE = "filieres";
  public static final String DISCIPLINE = "disciplines";

  public List<Object> getFonctions() {
    List<String> sources = disciplineRepository.findAllNonSarapisSources();
    if (sources.isEmpty()) return Collections.emptyList();

    ArrayList<Object> data = new ArrayList<>();

    Map<String, Object> all = new HashMap<>();
      all.put("source", ALL);
      all.put(FILIERE, getOfficial(ALL));
      data.add(all);

    sources.forEach(source -> {
      Map<String, Object> object = new HashMap<>();
      object.put("source", source);
      object.put(FILIERE, getOfficial(source));
      object.put("customMapping", getCustomMapping(source));
      data.add(object);
    });

    return data;
  }

  private List<TypeFonctionFiliereDto> getOfficial(String source) {
    // Recherche des filières, disciplines et fonctions les liants
    List<FonctionDto> fonctions;
      List<TypeFonctionFiliereDto> typesFonctionFiliere;
      List<DisciplineDto> disciplines;
    if (source.equals(ALL)) {
      fonctions = fonctionRepository.findWithoutSource();
      typesFonctionFiliere = typeFonctionFiliereRepository.findWithoutSource();
      disciplines = disciplineRepository.findWithoutSource();
    } else {
      fonctions = fonctionRepository.findBySource(source);
      typesFonctionFiliere = typeFonctionFiliereRepository.findBySource(source);
      disciplines = disciplineRepository.findBySource(source);
    }

    // Retourne les filières et disciplines s'il n'y a pas de fonction les liants
    if (fonctions.isEmpty()) return Collections.emptyList();

    // Ajout des disciplines aux filières
    typesFonctionFiliere = typesFonctionFiliere.stream()
      .map(typeFonctionFiliere -> {
        Set<Long> disciplineIds = fonctions.stream()
          .filter(fonction -> Objects.equals(fonction.getFiliere(), typeFonctionFiliere.getId()))
          .map(FonctionDto::getDisciplinePoste)
          .collect(Collectors.toSet());
        List<DisciplineDto> disciplinesInFiliere = disciplines.stream()
          .filter(discipline -> disciplineIds.contains(discipline.getId()) && !Objects.equals(discipline.getDisciplinePoste(), SANS_OBJET))
          .collect(Collectors.toList());
        typeFonctionFiliere.setDisciplines(disciplinesInFiliere);

        return typeFonctionFiliere;
      }).collect(Collectors.toList());

    if (source.equals(ALL)) return typesFonctionFiliere;
    // Retrait des filières sans disciplines
    return typesFonctionFiliere.stream()
      .filter(typeFonctionFiliere -> !typeFonctionFiliere.getDisciplines().isEmpty() && !Objects.equals(typeFonctionFiliere.getLibelleFiliere(), SANS_OBJET))
      .collect(Collectors.toList());
  }

  private Map<String, Object> getCustomMapping(String source) {
    Map<String, Object> data = new HashMap<>();
    ObjectMapper objectMapper = new ObjectMapper();

    // Lecture du fichier JSON
    List<AdditionalFonctionMapping> jsonFile;
    try {
      File resource = new ClassPathResource("mapping/additionalFonctionMapping.json").getFile();
      jsonFile = objectMapper.readValue(resource, new TypeReference<>() {
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Recherche du mapping
    AdditionalFonctionMapping mappingEntry = jsonFile.stream()
      .filter(af -> Objects.equals(af.getSource(), source))
      .findAny()
      .orElse(null);
    if (mappingEntry == null) return data;

    // Recherche des filières
    List<String> typeFonctionFiliereCodes = mappingEntry.getFilieres().stream()
      .map(AdditionalFonctionMappingFiliere::getCode)
      .collect(Collectors.toList());
    List<TypeFonctionFiliereDto> typesFonctionFiliere =
      typeFonctionFiliereRepository.findByCodeAndSourceSarapis(typeFonctionFiliereCodes, source);
    typesFonctionFiliere.sort(Comparator.comparingInt(tff -> typeFonctionFiliereCodes.indexOf(tff.getCodeFiliere())));

    // Ajout des disciplines aux filières
    if (!typesFonctionFiliere.isEmpty()) {
      typesFonctionFiliere = typesFonctionFiliere.stream()
        .map(typeFonctionFiliere -> {
          List<String> disciplineCodes = mappingEntry.getFilieres().stream()
            .filter(af -> Objects.equals(af.getCode(), typeFonctionFiliere.getCodeFiliere()))
            .findAny()
            .map(AdditionalFonctionMappingFiliere::getDisciplines)
            .orElse(new ArrayList<>());

          List<DisciplineDto> disciplines = disciplineRepository.findByCodeAndSourceSarapis(disciplineCodes, source);
          disciplines.sort(Comparator.comparingInt(discipline -> disciplineCodes.indexOf(discipline.getCode())));
          typeFonctionFiliere.setDisciplines(disciplines);

          return typeFonctionFiliere;
        })
        .collect(Collectors.toList());
      data.put(FILIERE, typesFonctionFiliere);
    }

    // Recherche des disciplines sans filières
    List<DisciplineDto> disciplines = disciplineRepository.findByCodeAndSourceSarapis(mappingEntry.getDisciplines(), source);
    if (!disciplines.isEmpty()) data.put(DISCIPLINE, disciplines);

    return data;
  }

  public List<FonctionDto> getPersonneFonctions(Long personneId) {
    return fonctionRepository.findByPersonne(personneId);
  }

  public List<FonctionDto> getAdditionalFonctions(Long personneId) {
    return fonctionRepository.findByPersonne(personneId).stream()
      .filter(fonction -> fonction.getSource().startsWith(Constants.SARAPISUI_))
      .collect(Collectors.toList());
  }

  public List<FonctionDto> getStructureFonctions(Long structureId) {
    return fonctionRepository.findByStructureId(structureId);
  }

  public List<TypeFonctionFiliereDto> getTypesFonctionFiliere(String source) {
    return typeFonctionFiliereRepository.findBySourceSarapis(source);
  }

  public List<DisciplineDto> getDisciplines(String source) {
    return disciplineRepository.findBySourceSarapis(source);
  }

  public boolean saveAdditionalFonctions(Long personneId, Long structureId, List<String> additional) {
    SimplePersonneDto personne = personneService.getPersonneSimple(personneId);
    if (personne == null) return false;
    if (!List.of(Etat.Invalide, Etat.Valide, Etat.Bloque).contains(personne.getEtat())) return false;
    String source = personne.getSource().startsWith(Constants.SARAPISUI_)
      ? personne.getSource()
      : Constants.SARAPISUI_ + personne.getSource();

    List<FonctionDto> additionalFonctions = getAdditionalFonctions(personneId);
    List<FonctionDto> requiredAdditional = additional.stream()
      .map(fonction -> {
        String[] split = fonction.split("-");

        return new FonctionDto(
          Long.parseLong(split[1]),
          Long.parseLong(split[0]),
          source,
          structureId
        );
      })
      .collect(Collectors.toList());

    List<FonctionDto> newAdditional = requiredAdditional.stream()
      .filter(fonction -> !additionalFonctions.contains(fonction))
      .collect(Collectors.toList());

    List<FonctionDto> deletedAdditional = additionalFonctions.stream()
      .filter(fonction -> !requiredAdditional.contains(fonction))
      .collect(Collectors.toList());

    APersonne apersonne = aPersonneRepository.findById(personneId).orElse(null);
    AStructure structure = aStructureRepository.findById(structureId).orElse(null);
    if (apersonne == null || structure == null) return false;

    int officialFonctionsInStructure = fonctionRepository.findByPersonne(personneId).stream()
      .filter(fonction -> !fonction.getSource().startsWith(Constants.SARAPISUI_) && Objects.equals(fonction.getStructure(), structureId))
      .collect(Collectors.toList()).size();

    boolean isInStructure = aPersonneAStructureRepository.isInStructure(personneId, structureId) > 0;
    boolean attachToStructure = !isInStructure && !newAdditional.isEmpty();
    boolean detachFromStructure = newAdditional.isEmpty() && !deletedAdditional.isEmpty() && additionalFonctions.size() == deletedAdditional.size() && officialFonctionsInStructure == 0;

    log.debug(
      "<==\n\t- additional fonctions : {}\n\t- require to add : {}\n\t- {} fonctions to add : {}\n\t- {} fonctions to delete : {}\n\t- is in structure : {}\n\t- attach to structure : {}\n\t- detach from structure : {}\n==>",
      additionalFonctions,
      requiredAdditional,
      newAdditional.size(),
      newAdditional,
      deletedAdditional.size(),
      deletedAdditional,
      isInStructure,
      attachToStructure,
      detachFromStructure
    );

    List<Fonction> saveAdditional = newAdditional.stream()
      .map(fonction -> {
        Discipline discipline = disciplineRepository.findById(fonction.getDisciplinePoste()).orElse(null);
        TypeFonctionFiliere filiere = typeFonctionFiliereRepository.findById(fonction.getFiliere()).orElse(null);

        return new Fonction(discipline, filiere, structure, apersonne, source);
      })
      .collect(Collectors.toList());

    List<Long> deleteAdditionalIds = deletedAdditional.stream()
      .map(fonction -> fonctionRepository.findId(fonction.getDisciplinePoste(), fonction.getFiliere(), personneId, fonction.getStructure()))
      .collect(Collectors.toList());

    if (!saveAdditional.isEmpty()) fonctionRepository.saveAll(saveAdditional);
    if (!deleteAdditionalIds.isEmpty()) fonctionRepository.deleteAllById(deleteAdditionalIds);
    if (!saveAdditional.isEmpty() || !deletedAdditional.isEmpty()) {
      fonctionRepository.flush();
      if (attachToStructure) aPersonneAStructureRepository2.insertInStructure(personneId, structureId);
      if (detachFromStructure) aPersonneAStructureRepository2.deleteFromStructure(personneId, structureId);
      apersonne.prePersist();
      apersonne.prePersistAPersonne();
      aPersonneRepository.saveAndFlush(apersonne);
    }

    return true;
  }

}
