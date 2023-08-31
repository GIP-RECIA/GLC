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

import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Etat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController()
@RequestMapping(value = "/api/config")
public class ConfigurationController {

  @Value("${security-configuration.cas.url.login}")
  private String casUrlLogin;
  @Value("${security-configuration.cas.url.logout}")
  private String casUrlLogout;
  @Value("${app.config.filiere.administrative}")
  private List<String> administrativeCodes;
  @Value("${app.config.filiere.teaching}")
  private List<String> teachingCodes;
  @Value("${app.config.sources.external.all}")
  private List<String> externalSourcesAll;

  @Value("${app.config.sources.external.4login}")
  private List<String> externalSources4Login;

  @GetMapping()
  public ResponseEntity<Object> getConfiguration() {
    Map<String, Object> data = new HashMap<>();

    List<CategoriePersonne> administrativeStaff = new ArrayList<>();
    administrativeStaff.add(CategoriePersonne.Enseignant);
    administrativeStaff.add(CategoriePersonne.Non_enseignant_collectivite_locale);
    administrativeStaff.add(CategoriePersonne.Non_enseignant_etablissement);
    administrativeStaff.add(CategoriePersonne.Non_enseignant_service_academique);
    data.put("administrativeStaff", administrativeStaff);

    data.put("administrativeCodes", administrativeCodes);
    data.put("teachingCodes", teachingCodes);

    data.put("externalSourcesAll", externalSourcesAll);
    data.put("externalSources4Login", externalSources4Login);

    List<CategoriePersonne> externalSources4LoginCategory = new ArrayList<>();
    externalSources4LoginCategory.add(CategoriePersonne.Enseignant);
    externalSources4LoginCategory.add(CategoriePersonne.Non_enseignant_service_academique);
    externalSources4LoginCategory.add(CategoriePersonne.Non_enseignant_etablissement);
    data.put("externalSources4LoginCategory", externalSources4LoginCategory);

    List<Etat> editAllowedStates = new ArrayList<>();
    editAllowedStates.add(Etat.Invalide);
    editAllowedStates.add(Etat.Valide);
    editAllowedStates.add(Etat.Bloque);
    editAllowedStates.add(Etat.Incertain);
    data.put("editAllowedStates", editAllowedStates);

    return new ResponseEntity<>(data, HttpStatus.OK);
  }

}
