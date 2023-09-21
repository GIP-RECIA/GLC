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
package fr.recia.glc.web.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.repositories.education.DisciplineRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.glc.models.mappers.AdditionalFonctionMapping;
import fr.recia.glc.models.mappers.AdditionalFonctionMappingFiliere;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.SANS_OBJET;

@Slf4j
@RestController
@RequestMapping(value = "/api/fonction")
public class FonctionController {

  @Autowired
  private FonctionRepository<Fonction> fonctionRepository;
  @Autowired
  private DisciplineRepository<Discipline> disciplineRepository;
  @Autowired
  private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;

  private static final String FILIERE = "filieres";
  private static final String DISCIPLINE = "disciplines";

  @GetMapping()
  public ResponseEntity<List<Object>> getFonctions() {
    ArrayList<Object> data = new ArrayList<>();

    List<String> sources = disciplineRepository.findAllNonSarapisSources();
    if (sources.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    sources.forEach(source -> {
      Map<String, Object> object = new HashMap<>();
      object.put("source", source);
      object.put(FILIERE, getOfficial(source));
      object.put("customMapping", getCustomMapping(source));
      data.add(object);
    });

    return new ResponseEntity<>(data, HttpStatus.OK);
  }

  private List<TypeFonctionFiliereDto> getOfficial(String source) {
    // Recherche des filières, disciplines et fonctions les liants
    List<FonctionDto> fonctions = fonctionRepository.findBySource(source);
    List<TypeFonctionFiliereDto> typesFonctionFiliere = typeFonctionFiliereRepository.findBySource(source);
    List<DisciplineDto> disciplines = disciplineRepository.findBySource(source);

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
      jsonFile = objectMapper.readValue(
        new File("src/main/resources/mapping/additionalFonctionMapping.json"),
        new TypeReference<>() {
        }
      );
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

}
