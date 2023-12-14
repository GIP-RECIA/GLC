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
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.dto.structure.EtablissementDto;
import fr.recia.glc.db.dto.structure.SimpleEtablissementDto;
import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.ldap.enums.PermissionType;
import fr.recia.glc.security.AuthoritiesConstants;
import fr.recia.glc.security.CustomUserDetails;
import fr.recia.glc.security.SecurityUtils;
import fr.recia.glc.services.beans.UserContextRole;
import fr.recia.glc.services.db.EtablissementService;
import fr.recia.glc.services.db.FonctionService;
import fr.recia.glc.services.db.PersonneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.SARAPISUI_;

@Slf4j
@RestController
@RequestMapping(value = "/api/structure/etablissement")
public class EtablissementController {

  @Autowired
  private EtablissementService etablissementService;
  @Autowired
  private FonctionService fonctionService;
  @Autowired
  private PersonneService personneService;

  private GLCProperties glcProperties;
  @Autowired
  private UserContextRole userContextRole;

  public EtablissementController(GLCProperties glcProperties) {
    this.glcProperties = glcProperties;
  }

  @GetMapping()
  public ResponseEntity<List<SimpleEtablissementDto>> getEtablissements() {
    final CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
    if (user == null) {
      log.trace("user is not authenticated -> throw an error to redirect on authentication");
      throw new AccessDeniedException("Access is denied to anonymous !");
    }

    List<SimpleEtablissementDto> etablissements;
    if (user.getRoles().contains(AuthoritiesConstants.ADMIN)) {
      etablissements = etablissementService.getEtablissements();
    } else {
      Pattern permissionPattern = glcProperties.getLdap().getGroupBranch().getStructureProperties().getUaiPattern();
      Set<String> allowedUAI = userContextRole.allowedStructures().stream()
        .map(structureKey -> {
          Matcher matcher = permissionPattern.matcher(structureKey.getKeyId());
          log.debug("structureKey : {}", structureKey.getKeyId());
          return matcher.find() ? matcher.group(1) : null;
        })
        .filter(s -> s != null)
        .collect(Collectors.toSet());

      etablissements = etablissementService.getEtablissements(allowedUAI);
    }

    etablissements = etablissements.stream()
      .map(etablissement -> {
        String[] split = etablissement.getNom().split("\\$");
        if (split.length > 1) {
          etablissement.setType(split[0]);
          etablissement.setVille(split[2]);
          etablissement.setNom(split[1]);
        }

        return etablissement;
      })
      .collect(Collectors.toList());
    if (etablissements.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return new ResponseEntity<>(etablissements, HttpStatus.OK);
  }

  @GetMapping(value = "/{id}")
  public ResponseEntity<EtablissementDto> getEtablissement(@PathVariable Long id) {
    final CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
    if (user == null) {
      log.trace("user is not authenticated -> throw an error to redirect on authentication");
      throw new AccessDeniedException("Access is denied to anonymous !");
    }

    EtablissementDto etablissement = etablissementService.getEtablissement(id);
    if (etablissement == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    if (!user.getRoles().contains(AuthoritiesConstants.ADMIN)) {
      Pattern permissionPattern = glcProperties.getLdap().getGroupBranch().getStructureProperties().getUaiPattern();
      Set<String> allowedUAI = userContextRole.allowedStructures().stream()
        .map(structureKey -> {
          Matcher matcher = permissionPattern.matcher(structureKey.getKeyId());
          log.debug("structureKey : {}", structureKey.getKeyId());
          return matcher.find() ? matcher.group(1) : null;
        })
        .filter(s -> s != null)
        .collect(Collectors.toSet());

      StructureKey structureKey = userContextRole.allowedStructures().stream()
        .filter(sk -> {
          Matcher matcher = permissionPattern.matcher(sk.getKeyId());
          return matcher.find() && matcher.group(1).equals(etablissement.getUai());
        })
        .findAny().orElse(null);

      etablissement.setPermission(userContextRole.getRoleFromContext(structureKey).getName());

      if (!allowedUAI.contains(etablissement.getUai())) return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    } else etablissement.setPermission(PermissionType.ADMIN.getName());
    String[] split = etablissement.getNom().split("\\$");
    if (split.length > 1) {
      etablissement.setType(split[0]);
      etablissement.setNom(split[1]);
    }
    List<SimplePersonneDto> etabPersonnes = personneService.getPersonnes(id);
    if (!user.getRoles().contains(AuthoritiesConstants.ADMIN)) {
      etabPersonnes = etabPersonnes.stream()
        .map((personne) -> {
          personne.setUid("");

          return personne;
        })
        .collect(Collectors.toList());
    }
    etablissement.setPersonnes(etabPersonnes);

    List<SimplePersonneDto> withoutFunction = fonctionService.getPersonnesWithoutFunctions(id);
    if (!user.getRoles().contains(AuthoritiesConstants.ADMIN)) {
      withoutFunction = withoutFunction.stream()
        .map((personne) -> {
          personne.setUid("");

          return personne;
        })
        .collect(Collectors.toList());
    }
    etablissement.setWithoutFunctions(withoutFunction);

    List<FonctionDto> fonctions = fonctionService.getStructureFonctions(id);
    List<TypeFonctionFiliereDto> typesFonctionFiliere = fonctionService.getTypesFonctionFiliere(etablissement.getSource());
    List<DisciplineDto> disciplines = fonctionService.getDisciplines(etablissement.getSource());

    log.debug(
      "<==\n\t- structure : {}\n\t- filieres : {}\n\t- disciplines : {}\n\t- personnes : {}\n\t- source : {}\n\t- filieres of source : {}\n\t- disciplines of source : {}\n==>",
      id,
      fonctions.stream().map(FonctionDto::getFiliere).collect(Collectors.toSet()),
      fonctions.stream().map(FonctionDto::getDisciplinePoste).collect(Collectors.toSet()),
      fonctions.stream().map(FonctionDto::getPersonne).collect(Collectors.toSet()),
      etablissement.getSource().startsWith(SARAPISUI_) ? etablissement.getSource() : etablissement.getSource() + " AND " + SARAPISUI_ + etablissement.getSource(),
      typesFonctionFiliere.stream().map(TypeFonctionFiliereDto::getId).collect(Collectors.toSet()),
      disciplines.stream().map(DisciplineDto::getId).collect(Collectors.toSet())
    );
    etablissement.setFilieres(id, etablissement.getSource(), etabPersonnes, fonctions, typesFonctionFiliere, disciplines);

    return new ResponseEntity<>(etablissement, HttpStatus.OK);
  }

}
