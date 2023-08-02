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

import fr.recia.glc.configuration.Constants;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.APersonneAStructureRepository2;
import fr.recia.glc.db.repositories.education.DisciplineRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestController()
@RequestMapping(value = "/api/personne")
public class PersonneController {
  @Autowired
  private APersonneAStructureRepository aPersonneAStructureRepository;

  @Autowired
  private APersonneRepository<APersonne> aPersonneRepository;
  @Autowired
  private FonctionRepository<Fonction> fonctionRepository;
  @Autowired
  private DisciplineRepository<Discipline> disciplineRepository;
  @Autowired
  private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;
  @Autowired
  private AStructureRepository<AStructure> aStructureRepository;
  @Autowired
  private APersonneAStructureRepository2 aPersonneAStructureRepository2;

  @GetMapping
  public ResponseEntity<List<SimplePersonneDto>> searchPersonne(@RequestParam(value = "name") String name) {
    List<SimplePersonneDto> personnes = aPersonneRepository.findByNameLike(name);
    if (personnes.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return new ResponseEntity<>(personnes, HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<PersonneDto> getPersonne(@PathVariable Long id) {
    PersonneDto personne = aPersonneRepository.findByPersonneId(id);
    if (personne == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    List<FonctionDto> fonctions = fonctionRepository.findByPersonne(id);
    if (!fonctions.isEmpty()) {
      personne.setFonctions(fonctions.stream().filter(fonction -> !fonction.getSource().startsWith(Constants.SARAPISUI_)).collect(Collectors.toList()));
      personne.setAdditionalFonctions(fonctions.stream().filter(fonction -> fonction.getSource().startsWith(Constants.SARAPISUI_)).collect(Collectors.toList()));
    }

    return new ResponseEntity<>(personne, HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/fonction")
  public ResponseEntity setPersonneAdditionalFonctions(@PathVariable Long id, @RequestBody Map<String, Object> body) throws Exception {
    SimplePersonneDto personne = aPersonneRepository.findByPersonneIdSimple(id);
    if (personne == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    String source = personne.getSource().startsWith(Constants.SARAPISUI_)
      ? personne.getSource()
      : Constants.SARAPISUI_ + personne.getSource();
    List<FonctionDto> additionalFonctions = fonctionRepository.findByPersonne(id).stream()
      .filter(fonction -> fonction.getSource().startsWith(Constants.SARAPISUI_))
      .collect(Collectors.toList());

    Long structureId = (long) (int) body.get("structureId");
    List<FonctionDto> requiredAdditional = ((List<String>) body.get("additional")).stream()
      .map(fonction -> {
        String[] split = fonction.split("-");

        return new FonctionDto(
          Long.parseLong(split[1]),
          Long.parseLong(split[0]),
          source
        );
      })
      .collect(Collectors.toList());

    List<FonctionDto> newAdditional = requiredAdditional.stream()
      .filter(fonction -> !additionalFonctions.contains(fonction))
      .collect(Collectors.toList());

    List<FonctionDto> deletedAdditional = additionalFonctions.stream()
      .filter(fonction -> !requiredAdditional.contains(fonction))
      .collect(Collectors.toList());

    log.debug("{} fonctions to add : {}", newAdditional.size(), newAdditional);
    log.debug("{} fonctions to delete : {}", deletedAdditional.size(), deletedAdditional);

    APersonne apersonne = aPersonneRepository.findById(id).orElse(null);
    AStructure structure = aStructureRepository.findById(structureId).orElse(null);

    boolean isInStructure = aPersonneAStructureRepository.isInStructure(id, structureId) > 0;

    if (!isInStructure) {
      log.debug("User is not in structure !");
      aPersonneAStructureRepository2.insertInStructure(id, structureId);
    }

    int officialFonctionsInStructure = fonctionRepository.findByPersonne(id).stream()
      .filter(fonction -> !fonction.getSource().startsWith(Constants.SARAPISUI_) && Objects.equals(fonction.getStructure(), structureId))
      .collect(Collectors.toList()).size();
    if (newAdditional.isEmpty() && !deletedAdditional.isEmpty() && officialFonctionsInStructure == 0) {
      log.debug("Detach user from structure !");
      aPersonneAStructureRepository2.deleteFromStructure(id, structureId);
    }

    List<Fonction> saveAdditional = newAdditional.stream()
      .map(fonction -> {
        log.debug("DisciplineId={}, filiereId={}", fonction.getDisciplinePoste(), fonction.getFiliere());
        Discipline discipline = disciplineRepository.findById(fonction.getDisciplinePoste()).orElse(null);
        TypeFonctionFiliere filiere = typeFonctionFiliereRepository.findById(fonction.getFiliere()).orElse(null);

        return new Fonction(discipline, filiere, structure, apersonne, source);
      })
      .collect(Collectors.toList());

    List<Long> deleteAdditionalIds = deletedAdditional.stream()
      .map(fonction -> fonctionRepository.findId(fonction.getDisciplinePoste(), fonction.getFiliere(), id, fonction.getStructure()))
      .collect(Collectors.toList());

    if (!saveAdditional.isEmpty()) fonctionRepository.saveAll(saveAdditional);
    if (!deleteAdditionalIds.isEmpty()) fonctionRepository.deleteAllById(deleteAdditionalIds);
    if (!saveAdditional.isEmpty() || !deletedAdditional.isEmpty()) fonctionRepository.flush();

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
