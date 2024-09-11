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
import fr.recia.glc.db.entities.fonction.QFonction;
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
import fr.recia.glc.pojo.JsonFonction;
import fr.recia.glc.utils.DateUtils;
import fr.recia.glc.utils.SourceUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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

    final ArrayList<Object> data = new ArrayList<>();

    final Map<String, Object> all = new HashMap<>();
    all.put("source", ALL);
    all.put(FILIERE, getOfficial(
      typesFonctionFiliere,
      disciplines,
      new ArrayList<>(new LinkedHashSet<>(Stream.concat(
        fonctions.stream(),
        getFonctionsFromMapping(typesFonctionFiliere, disciplines).stream()
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
      return getDisciplinesByFiliere(
        typesFonctionFiliere.stream()
          .filter(typeFonctionFiliere -> areSourcesEquals(typeFonctionFiliere.getSource(), source))
          .collect(Collectors.toList()),
        disciplines.stream()
          .filter(discipline -> areSourcesEquals(discipline.getSource(), source))
          .collect(Collectors.toList()),
        fonctions.stream()
          .filter(fonction -> areSourcesEquals(fonction.getSource(), source, false))
          .collect(Collectors.toList()),
        source
      ).stream()
        .filter(typeFonctionFiliere -> !typeFonctionFiliere.getDisciplines().isEmpty())
        .collect(Collectors.toList());
    }

    return getDisciplinesByFiliere(typesFonctionFiliere, disciplines, fonctions, source);
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

    final List<TypeFonctionFiliereDto> tmpTypesFonctionFiliere = typesFonctionFiliere.stream()
      .map(TypeFonctionFiliereDto::new)
      .collect(Collectors.toList());
    final List<DisciplineDto> tmpDisciplines = disciplines.stream()
      .map(DisciplineDto::new)
      .collect(Collectors.toList());
    final List<FonctionDto> tmpFonctions = fonctions.stream()
      .map(FonctionDto::new)
      .collect(Collectors.toList());

    return tmpTypesFonctionFiliere.stream()
      .filter(typeFonctionFiliere -> !typesFonctionFiliereToNotInclue.contains(typeFonctionFiliere.getLibelleFiliere()))
      .map(typeFonctionFiliere -> {
        Set<Long> disciplineIds = tmpFonctions.stream()
          .filter(fonction -> Objects.equals(fonction.getFiliere(), typeFonctionFiliere.getId()))
          .map(FonctionDto::getDiscipline)
          .collect(Collectors.toSet());
        List<DisciplineDto> disciplinesInFiliere = tmpDisciplines.stream()
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

  public boolean saveAdditionalFonction(Long personneId, Long structureId, String additional, String requiredAction) {
    final AStructure aStructure = aStructureRepository.findById(structureId).orElse(null);
    if (aStructure == null) return false;

    final String source = SourceUtils.getOfficialSource(aStructure.getCleJointure().getSource());

    final String[] split = additional.split("-");
    final Long filiere = typeFonctionFiliereRepository.findByCode(split[0], source);
    final Long discipline = disciplineRepository.findByCode(split[1], source);
    if (filiere == null || discipline == null) return false;
    final String fonction = filiere + "-" + discipline;

    return saveAdditionalFonctions(personneId, structureId, List.of(fonction), List.of(), requiredAction);
  }

  public boolean saveAdditionalFonctions(
    Long personneId,
    Long structureId,
    List<String> toAddFunctions,
    List<String> toDeleteFunctions,
    String requiredAction
  ) {
    final APersonne aPersonne = aPersonneRepository.findById(personneId).orElse(null);
    final AStructure aStructure = aStructureRepository.findById(structureId).orElse(null);
    if (aPersonne == null
      || aStructure == null
      || !List.of(Etat.Invalide, Etat.Valide, Etat.Bloque).contains(aPersonne.getEtat())
    ) return false;

    final String source = Constants.SARAPISUI_ + SourceUtils.getOfficialSource(aStructure.getCleJointure().getSource());

    final List<FonctionDto> toAddAdditional = toFonctionDto(personneId, toAddFunctions.stream().map(JsonFonction::new).collect(Collectors.toList()), source, structureId);
    final List<FonctionDto> toDeleteAdditional = toFonctionDto(personneId, toDeleteFunctions.stream().map(JsonFonction::new).collect(Collectors.toList()), source, structureId);

    if (!toAddAdditional.isEmpty()) {
      fonctionRepository.saveAll(toAddAdditional.stream()
        .map(fonction -> {
          TypeFonctionFiliere filiere = typeFonctionFiliereRepository.findById(fonction.getFiliere()).orElse(null);
          Discipline discipline = disciplineRepository.findById(fonction.getDiscipline()).orElse(null);

          return new Fonction(discipline, filiere, aStructure, aPersonne, source);
        })
        .collect(Collectors.toList()));
    }

    if (!toDeleteAdditional.isEmpty()) {
      fonctionRepository.deleteAllById(toDeleteAdditional.stream()
        .map(fonction -> fonctionRepository.findId(fonction.getFiliere(), fonction.getDiscipline(), personneId, fonction.getStructure(), fonction.getSource()))
        .collect(Collectors.toList()));
    }

    if (!toAddAdditional.isEmpty() || !toDeleteAdditional.isEmpty()) {
      fonctionRepository.flush();

      boolean isInStructure = aPersonneAStructureRepository.isInStructure(personneId, structureId) > 0;
      int officialFonctionsInStructure = (int) fonctionRepository.findByPersonne(personneId).stream()
        .filter(fonction -> !SourceUtils.isSourceOfficial(fonction.getSource()) && Objects.equals(fonction.getStructure(), structureId))
        .count();

      switch (requiredAction) {
        case "attach":
          if (isInStructure || toAddAdditional.isEmpty()) {
            log.error("Unable to attach user {} to structure {}", aPersonne.getId(), aStructure.getId());
            break;
          }
          aPersonneAStructureRepository2.insertInStructure(personneId, structureId);
          break;
        case "detach":
          if (!isInStructure || !toAddAdditional.isEmpty() || toDeleteAdditional.isEmpty() || officialFonctionsInStructure > 0) {
            log.error("Unable to detach user {} to structure {}", aPersonne.getId(), aStructure.getId());
            break;
          }
          aPersonneAStructureRepository2.deleteFromStructure(personneId, structureId);
          break;
        default:
          break;
      }
      aPersonne.prePersist();
      aPersonne.prePersistAPersonne();
      aPersonneRepository.saveAndFlush(aPersonne);
    }

    return true;
  }

  public boolean setAdditional(Long personneId, Long structureId, JsonFonction toAdd, String toDelete) {
    final APersonne aPersonne = aPersonneRepository.findById(personneId).orElse(null);
    final AStructure aStructure = aStructureRepository.findById(structureId).orElse(null);
    if (aPersonne == null
      || aStructure == null
      || !List.of(Etat.Invalide, Etat.Valide, Etat.Bloque).contains(aPersonne.getEtat())
    ) return false;

    final String source = Constants.SARAPISUI_ + SourceUtils.getOfficialSource(aStructure.getCleJointure().getSource());
    final boolean isInStructure = personneService.isInStructure(personneId, structureId);
    log.debug(
      "setAdditional (userId : {}, structureId : {}, source : {}, isInStructure : {}, toAdd : {}, toDelete, {})",
      aPersonne.getId(), aStructure.getId(), source, isInStructure, toAdd, toDelete
    );

    if (toAdd != null) {
      FonctionDto fonctionDto = toFonctionDto(personneId, toAdd, source, structureId);
      try {
        fonctionDto.setDateFin(DateUtils.getDate(toAdd.getDate()));
      } catch (ParseException e) {
        log.error("Unable to set end date");
      }
      Fonction fonction = getFonctionOrNewFonction(fonctionDto, aStructure, aPersonne);
      if (fonctionDto.getDateFin() != null && fonctionDto.getDateFin() != fonction.getDateFin())
        fonction.setDateFin(fonctionDto.getDateFin());
      fonctionRepository.save(fonction);
      if (!isInStructure) aPersonneAStructureRepository2.insertInStructure(personneId, structureId);
    }

    if (toDelete != null) {
      final FonctionDto fonctionDto = toFonctionDto(personneId, toDelete, source, structureId);
      final Fonction fonction = getFonction(fonctionDto);
      if (fonction != null) fonctionRepository.delete(fonction);
      if (isInStructure && !personneService.hasFunctionsInStructure(personneId, structureId, List.of(fonction.getId())))
        aPersonneAStructureRepository2.deleteFromStructure(personneId, structureId);
    }

    fonctionRepository.flush();

    aPersonne.prePersist();
    aPersonne.prePersistAPersonne();
    aPersonneRepository.saveAndFlush(aPersonne);

    return true;
  }

  private Fonction getFonction(FonctionDto fonctionDto) {
    return fonctionRepository.findOne(
      QFonction.fonction.filiere.id.eq(fonctionDto.getFiliere())
        .and(QFonction.fonction.disciplinePoste.id.eq(fonctionDto.getDiscipline()))
        .and(QFonction.fonction.personne.id.eq(fonctionDto.getPersonne()))
        .and(QFonction.fonction.structure.id.eq(fonctionDto.getStructure()))
        .and(QFonction.fonction.source.eq(fonctionDto.getSource()))
    ).orElse(null);
  }

  private Fonction getFonctionOrNewFonction(FonctionDto fonctionDto, AStructure aStructure, APersonne aPersonne) {
    Fonction fonction = getFonction(fonctionDto);
    if (fonction == null) {
      fonction = new Fonction(
        disciplineRepository.findById(fonctionDto.getDiscipline()).orElse(null),
        typeFonctionFiliereRepository.findById(fonctionDto.getFiliere()).orElse(null),
        aStructure,
        aPersonne,
        fonctionDto.getSource(),
        fonctionDto.getDateFin()
      );
    }
    return fonction;
  }

  private FonctionDto toFonctionDto(Long personneId, String code, String source, Long structureId) {
    String[] split = code.split("-");

    return new FonctionDto(
      personneId,
      Long.parseLong(split[0]),
      Long.parseLong(split[1]),
      source,
      structureId
    );
  }

  private FonctionDto toFonctionDto(Long personneId, JsonFonction code, String source, Long structureId) {
    String[] split = code.getFonction().split("-");
    Date date = null;

    if (code.getDate() != null && !code.getDate().isEmpty()) {
      try {
        date = DateUtils.getDate(code.getDate());
      } catch (ParseException e) {
        log.error(String.valueOf(e));
      }
    }

    return new FonctionDto(
      personneId,
      Long.parseLong(split[0]),
      Long.parseLong(split[1]),
      source,
      structureId,
      date
    );
  }

  private List<FonctionDto> toFonctionDto(Long personneId, List<JsonFonction> codes, String source, Long structureId) {
    return codes.stream()
      .map(code -> toFonctionDto(personneId, code, source, structureId))
      .collect(Collectors.toList());
  }

  public boolean isDiscipline(Long structureId, String disciplineCode) {
    return fonctionRepository.nbDiscipline(structureId, disciplineCode) > 0;
  }

}
