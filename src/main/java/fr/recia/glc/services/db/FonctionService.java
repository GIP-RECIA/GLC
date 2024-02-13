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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.recia.glc.configuration.Constants.SANS_OBJET;
import static fr.recia.glc.utils.SourceUtils.areSourcesEquals;

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
    final List<String> sources = disciplineRepository.findAllNonSarapisSources();
    if (sources.isEmpty()) return Collections.emptyList();

    final List<TypeFonctionFiliereDto> typesFonctionFiliere = typeFonctionFiliereRepository.findWithoutSource();
    final List<DisciplineDto> disciplines = disciplineRepository.findWithoutSource();
    final List<FonctionDto> fonctions = fonctionRepository.findWithoutSource();
    final List<FonctionDto> customMappingfonctions = getFonctionsFromMapping(typesFonctionFiliere, disciplines);

    final ArrayList<Object> data = new ArrayList<>();

    final Map<String, Object> all = new HashMap<>();
    all.put("source", ALL);
    all.put(FILIERE, getOfficial(
      typesFonctionFiliere,
      disciplines,
      new ArrayList<>(new LinkedHashSet<>(Stream.concat(
        fonctions.stream(),
        customMappingfonctions.stream()
      ).collect(Collectors.toList()))),
      ALL));
    data.add(all);

    sources.forEach(source -> {
      final Map<String, Object> object = new HashMap<>();
      object.put("source", source);
      object.put(FILIERE, getOfficial(typesFonctionFiliere, disciplines, fonctions, source));
      object.put("customMapping", getCustomMapping(source));
      data.add(object);
    });

    return data;
  }

  private List<FonctionDto> getFonctionsFromMapping(
    List<TypeFonctionFiliereDto> typesFonctionFiliere,
    List<DisciplineDto> disciplines
  ) {
    List<FonctionDto> data = new ArrayList<>();
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

    jsonFile.forEach(file -> {
      file.getFilieres().forEach(filiere -> {
        TypeFonctionFiliereDto filiereDto = typesFonctionFiliere.stream()
          .filter(tffdto -> Objects.equals(tffdto.getCodeFiliere(), filiere.getCode()) && areSourcesEquals(tffdto.getSource(), file.getSource(), false))
          .findAny()
          .orElse(null);

        filiere.getDisciplines().forEach(discipline -> {
          DisciplineDto disciplineDto = disciplines.stream()
            .filter(ddto -> Objects.equals(ddto.getCode(), discipline) && areSourcesEquals(ddto.getSource(), file.getSource(), false))
            .findAny()
            .orElse(null);

          if (disciplineDto != null && filiereDto != null)
            data.add(new FonctionDto(disciplineDto.getId(), filiereDto.getId(), file.getSource()));
        });
      });
    });

    return data;
  }

  private List<TypeFonctionFiliereDto> getOfficial(
    List<TypeFonctionFiliereDto> typesFonctionFiliere,
    List<DisciplineDto> disciplines,
    List<FonctionDto> fonctions,
    String source
  ) {
    if (!source.equals(ALL)) {
      typesFonctionFiliere = typesFonctionFiliere.stream()
        .filter(typeFonctionFiliere -> areSourcesEquals(typeFonctionFiliere.getSource(), source))
        .collect(Collectors.toList());
      disciplines = disciplines.stream()
        .filter(discipline -> areSourcesEquals(discipline.getSource(), source))
        .collect(Collectors.toList());
      fonctions = fonctions.stream()
        .filter(fonction -> areSourcesEquals(fonction.getSource(), source, false))
        .collect(Collectors.toList());
    }

    List<TypeFonctionFiliereDto> disciplinesByFiliere = getDisciplinesByFiliere(typesFonctionFiliere, disciplines, fonctions, source);

    if (!source.equals(ALL))
      return disciplinesByFiliere.stream()
        .filter(typeFonctionFiliere -> !typeFonctionFiliere.getDisciplines().isEmpty())
        .collect(Collectors.toList());

    return disciplinesByFiliere;
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

  private List<TypeFonctionFiliereDto> getDisciplinesByFiliere(
    List<TypeFonctionFiliereDto> typesFonctionFiliere,
    List<DisciplineDto> disciplines,
    List<FonctionDto> fonctions,
    String source
  ) {
    return getDisciplinesByFiliere(typesFonctionFiliere, disciplines, fonctions, source, List.of(SANS_OBJET), List.of(SANS_OBJET));
  }

  private List<TypeFonctionFiliereDto> getDisciplinesByFiliere(
    List<TypeFonctionFiliereDto> typesFonctionFiliere,
    List<DisciplineDto> disciplines,
    List<FonctionDto> fonctions,
    String source,
    List<String> typesFonctionFiliereToNotInclue,
    List<String> disciplinesToNotInclue
  ) {
    if (fonctions.isEmpty()) return Collections.emptyList();

    return typesFonctionFiliere.stream()
      .filter(typeFonctionFiliere -> !typesFonctionFiliereToNotInclue.contains(typeFonctionFiliere.getLibelleFiliere()))
      .map(typeFonctionFiliere -> {
        Set<Long> disciplineIds = fonctions.stream()
          .filter(fonction -> Objects.equals(fonction.getFiliere(), typeFonctionFiliere.getId()))
          .map(FonctionDto::getDisciplinePoste)
          .collect(Collectors.toSet());
        List<DisciplineDto> disciplinesInFiliere = disciplines.stream()
          .filter(discipline -> disciplineIds.contains(discipline.getId()) && !disciplinesToNotInclue.contains(discipline.getDisciplinePoste()))
          .collect(Collectors.toList());
        typeFonctionFiliere.setDisciplines(disciplinesInFiliere);

        return typeFonctionFiliere;
      })
      .collect(Collectors.toList());
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

  public List<SimplePersonneDto> getPersonnesWithoutFunctions(Long structureId) {
    final List<Long> personnesIds = fonctionRepository.findPersonnesWithoutFunctions(structureId);
    return aPersonneRepository.findByPersonneIds(new HashSet<>(personnesIds));
  }

  public boolean saveAdditionalFonction(Long personneId, Long structureId, String additional) {
    SimplePersonneDto personne = personneService.getPersonneSimple(personneId);
    if (personne == null) return false;
    if (!List.of(Etat.Invalide, Etat.Valide, Etat.Bloque).contains(personne.getEtat())) return false;
    String source = personne.getSource().startsWith(Constants.SARAPISUI_)
      ? personne.getSource().substring(Constants.SARAPISUI_.length())
      : personne.getSource();

    final String[] split = additional.split("-");
    final Long filiere = typeFonctionFiliereRepository.findByCode(split[0], source);
    final Long discipline = disciplineRepository.findByCode(split[1], source);
    final String fonction = filiere + "-" + discipline;
    log.debug("==>\n - {}\n - {}\n - {}\n<==", split, source, fonction);

    return saveAdditionalFonctions(personneId, structureId, List.of(fonction));
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

  public boolean isDiscipline(Long structureId, String disciplineCode) {
    return fonctionRepository.nbDiscipline(structureId, disciplineCode) > 0;
  }

}
