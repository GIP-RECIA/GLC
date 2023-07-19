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

import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
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

@Slf4j
@RestController()
@RequestMapping(value = "/api/personne")
public class PersonneController {

  @Autowired
  private APersonneRepository<APersonne> aPersonneRepository;
  @Autowired
  private FonctionRepository<Fonction> fonctionRepository;

  @GetMapping
  public ResponseEntity<String> searchPersonne(
    @RequestParam(value = "name") String name
  ) {
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<PersonneDto> getPersonne(@PathVariable Long id) {
    PersonneDto personne = aPersonneRepository.findByPersonneId(id);
    List<FonctionDto> fonctions = fonctionRepository.findByPersonneIdAndStructure(id, personne.getStructure());
    personne.setFonctions(fonctions.stream().filter(fonction -> !fonction.getSource().startsWith("SarapisUi_")).toList());
    personne.setAdditionalFonctions(fonctions.stream().filter(fonction -> fonction.getSource().startsWith("SarapisUi_")).toList());

    return new ResponseEntity<>(personne, HttpStatus.OK);
  }

  @PostMapping(value = "/{id}/fonction")
  public ResponseEntity<List<FonctionDto>> setPersonneAdditionalFonctions(@PathVariable Long id, @RequestBody Map<String, Object> body) {
    PersonneDto personne = aPersonneRepository.findByPersonneId(id);
    String source = personne.getSource().startsWith("SarapisUI_")
      ? personne.getSource()
      : "SarapisUI_" + personne.getSource();

    List<FonctionDto> fonctions = ((List<String>) body.get("additional")).stream()
      .map(fonction -> {
        String[] split = fonction.split("-");

        return new FonctionDto(
          Long.parseLong(split[0]),
          Long.parseLong(split[1]),
          source
        );
      })
      .toList();

    return new ResponseEntity<>(fonctions, HttpStatus.OK);
  }

}
