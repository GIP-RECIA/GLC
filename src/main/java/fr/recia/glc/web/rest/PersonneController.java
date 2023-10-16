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
import fr.recia.glc.security.AuthoritiesConstants;
import fr.recia.glc.security.CustomUserDetails;
import fr.recia.glc.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
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
@RestController
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
    final CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
    if (user == null) {
      log.trace("user is not authenticated -> throw an error to redirect on authentication");
      throw new AccessDeniedException("Access is denied to anonymous !");
    }

    List<SimplePersonneDto> personnes = aPersonneRepository.findByNameLike(name);
    if (personnes.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    if (!user.getRoles().contains(AuthoritiesConstants.ADMIN)) {
      personnes = personnes.stream()
        .map(personne -> {
          personne.setUid("");

          return personne;
        })
        .collect(Collectors.toList());
    }

    return new ResponseEntity<>(personnes, HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<PersonneDto> getPersonne(@PathVariable Long id) {
    final CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
    if (user == null) {
      log.trace("user is not authenticated -> throw an error to redirect on authentication");
      throw new AccessDeniedException("Access is denied to anonymous !");
    }

    PersonneDto personne = aPersonneRepository.findByPersonneId(id);
    if (personne == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    if (!user.getRoles().contains(AuthoritiesConstants.ADMIN)) {
      personne.setUid("");
    }
    List<FonctionDto> fonctions = fonctionRepository.findByPersonne(id);
    if (!fonctions.isEmpty()) {
      personne.setFonctions(fonctions.stream().filter(fonction -> !fonction.getSource().startsWith(Constants.SARAPISUI_)).collect(Collectors.toList()));
      personne.setAdditionalFonctions(fonctions.stream().filter(fonction -> fonction.getSource().startsWith(Constants.SARAPISUI_)).collect(Collectors.toList()));
    }

    return new ResponseEntity<>(personne, HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/fonction")
  public ResponseEntity setPersonneAdditionalFonctions(@PathVariable Long id, @RequestBody Map<String, Object> body) throws Exception {
    final CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
    if (user == null) {
      log.trace("user is not authenticated -> throw an error to redirect on authentication");
      throw new AccessDeniedException("Access is denied to anonymous !");
    }

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

    APersonne apersonne = aPersonneRepository.findById(id).orElse(null);
    AStructure structure = aStructureRepository.findById(structureId).orElse(null);
    if (apersonne == null || structure == null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    int officialFonctionsInStructure = fonctionRepository.findByPersonne(id).stream()
      .filter(fonction -> !fonction.getSource().startsWith(Constants.SARAPISUI_) && Objects.equals(fonction.getStructure(), structureId))
      .collect(Collectors.toList()).size();

    boolean isInStructure = aPersonneAStructureRepository.isInStructure(id, structureId) > 0;
    boolean attachToStructure = !isInStructure && !newAdditional.isEmpty();
    boolean detachFromStructure = newAdditional.isEmpty() && !deletedAdditional.isEmpty() && additionalFonctions.size() == deletedAdditional.size() && officialFonctionsInStructure == 0;

    if (attachToStructure) aPersonneAStructureRepository2.insertInStructure(id, structureId);
    if (detachFromStructure) aPersonneAStructureRepository2.deleteFromStructure(id, structureId);

    if (log.isDebugEnabled())
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
      .map(fonction -> fonctionRepository.findId(fonction.getDisciplinePoste(), fonction.getFiliere(), id, fonction.getStructure()))
      .collect(Collectors.toList());

    if (!saveAdditional.isEmpty()) fonctionRepository.saveAll(saveAdditional);
    if (!deleteAdditionalIds.isEmpty()) fonctionRepository.deleteAllById(deleteAdditionalIds);
    if (!saveAdditional.isEmpty() || !deletedAdditional.isEmpty()) fonctionRepository.flush();

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
