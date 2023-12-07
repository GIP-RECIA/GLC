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

import fr.recia.glc.configuration.GLCProperties;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.ldap.enums.PermissionType;
import lombok.extern.slf4j.Slf4j;
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
@RestController
@RequestMapping(value = "/api/config")
public class ConfigurationController {

  private final GLCProperties glcProperties;

  public ConfigurationController(GLCProperties glcProperties) {
    this.glcProperties = glcProperties;
  }

  @GetMapping()
  public ResponseEntity<Object> getConfiguration() {
    Map<String, Object> data = new HashMap<>();

    data.put("administrativeStaff", List.of(
      CategoriePersonne.Non_enseignant_etablissement
    ));

    data.put("administrativeCodes", glcProperties.getCustomConfig().getFiliereAdministrative());
    data.put("teachingCodes", glcProperties.getCustomConfig().getFiliereTeaching());
    data.put("externalSourcesAll", glcProperties.getCustomConfig().getSourcesExternalAll());
    data.put("externalSources4Login", glcProperties.getCustomConfig().getSourcesExternal4login());

    data.put("externalSources4LoginCategory", List.of(
      CategoriePersonne.Enseignant,
      CategoriePersonne.Non_enseignant_service_academique,
      CategoriePersonne.Non_enseignant_etablissement
    ));

    data.put("editAllowedStates", List.of(
      Etat.Invalide,
      Etat.Valide,
      Etat.Bloque
    ));

    data.put("filterAccountStates", List.of(
      Etat.Invalide,
      Etat.Valide,
      Etat.Bloque,
      Etat.Delete,
      Etat.Incertain
    ));

    data.put("permissionTypes", List.of(
      PermissionType.ADMIN.getName(),
      PermissionType.MANAGER.getName(),
      PermissionType.MANAGER_BRANCH.getName(),
      PermissionType.LOOKOVER.getName(),
      PermissionType.LOOKOVER_BRANCH.getName()
    ));

    return new ResponseEntity<>(data, HttpStatus.OK);
  }

}
