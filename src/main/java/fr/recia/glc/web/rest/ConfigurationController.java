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

  @GetMapping()
  public ResponseEntity<Object> getConfiguration() {
    Map<String, Object> data = new HashMap<>();

    data.put("casUrlLogin", casUrlLogin);
    data.put("casUrlLogout", casUrlLogout);

    List<CategoriePersonne> administrativeStaff = new ArrayList<>();
    administrativeStaff.add(CategoriePersonne.Enseignant);
    administrativeStaff.add(CategoriePersonne.Non_enseignant_collectivite_locale);
    administrativeStaff.add(CategoriePersonne.Non_enseignant_etablissement);
    administrativeStaff.add(CategoriePersonne.Non_enseignant_service_academique);
    data.put("administrativeStaff", administrativeStaff);

    data.put("administrativeCodes", administrativeCodes);
    data.put("teachingCodes", teachingCodes);

    return new ResponseEntity<>(data, HttpStatus.OK);
  }

}
