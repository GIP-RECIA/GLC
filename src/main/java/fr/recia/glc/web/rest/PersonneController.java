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

import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.pojo.JsonAdditionalFonctionBody;
import fr.recia.glc.pojo.JsonAdditionalFonctionOldBody;
import fr.recia.glc.services.db.FonctionService;
import fr.recia.glc.services.db.PersonneService;
import fr.recia.glc.web.interceptor.bean.UserHolder;
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
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/personne")
public class PersonneController {

  @Autowired
  private FonctionService fonctionService;
  @Autowired
  private PersonneService personneService;

  @Autowired
  private UserHolder userHolder;

  @GetMapping
  public ResponseEntity<List<SimplePersonneDto>> searchPersonne(@RequestParam(value = "name") String name) {
    List<SimplePersonneDto> personnes = personneService.searchPersoonne(name, userHolder.isAdmin());
    if (personnes.isEmpty()) return new ResponseEntity<>(HttpStatus.OK);
    if (!userHolder.isAdmin()) {
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
    PersonneDto personne = personneService.getPersonne(id);
    if (personne == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    if (!userHolder.isAdmin()) personne.setUid("");
    personne.setAllFonctions(fonctionService.getPersonneFonctions(id));

    return new ResponseEntity<>(personne, HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/fonction")
  public ResponseEntity setPersonneAdditionalFonctions(@PathVariable Long id, @RequestBody JsonAdditionalFonctionOldBody body) throws Exception {
    if (!body.postDataOk()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    boolean success;
    if (body.getAdditionalCode() == null) {
      success = fonctionService.saveAdditionalFonctions(
        id,
        body.getStructureId(),
        body.getToAddFunctions(),
        body.getToDeleteFunctions(),
        body.getRequiredAction()
      );
    } else {
      success = fonctionService.saveAdditionalFonction(
        id,
        body.getStructureId(),
        body.getAdditionalCode(),
        body.getRequiredAction()
      );
    }
    if (!success) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/fonction/v2")
  public ResponseEntity setPersonneAdditionals(@PathVariable Long id, @RequestBody JsonAdditionalFonctionBody body) throws Exception {
    if (!body.postDataOk()) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    boolean success;
    success = fonctionService.setAdditional(
      id,
      body.getStructureId(),
      body.getToAdd(),
      body.getToDelete()
    );
    if (!success) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    return new ResponseEntity<>(HttpStatus.OK);
  }

}
