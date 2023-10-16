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
import fr.recia.glc.db.entities.APersonneAStructure;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.education.DisciplineRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.db.repositories.structure.EtablissementRepository;
import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.ldap.enums.PermissionType;
import fr.recia.glc.security.AuthoritiesConstants;
import fr.recia.glc.security.CustomUserDetails;
import fr.recia.glc.security.SecurityUtils;
import fr.recia.glc.services.beans.UserContextRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.SANS_OBJET;
import static fr.recia.glc.configuration.Constants.SARAPISUI_;

@Slf4j
@RestController
@RequestMapping(value = "/api/structure/etablissement")
public class EtablissementController {

  @Autowired
  private EtablissementRepository<Etablissement> etablissementRepository;
  @Autowired
  private FonctionRepository<Fonction> fonctionRepository;
  @Autowired
  private APersonneRepository<APersonne> aPersonneRepository;
  @Autowired
  private DisciplineRepository<Discipline> disciplineRepository;
  @Autowired
  private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;
  @Autowired
  private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;

  private GLCProperties glcProperties;
  @Inject
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
      etablissements = etablissementRepository.findAllEtablissements();
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

      etablissements = etablissementRepository.findAllowedEtablissements(allowedUAI);
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

    EtablissementDto etablissement = etablissementRepository.findByEtablissementId(id);
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
    List<Long> personnesIds = aPersonneAStructureRepository.findPersonneByStructureId(id);
    List<SimplePersonneDto> etabPersonnes = aPersonneRepository.findByPersonneIds(personnesIds);
    if (!user.getRoles().contains(AuthoritiesConstants.ADMIN)) {
      etabPersonnes = etabPersonnes.stream()
        .map((personne) -> {
          personne.setUid("");

          return personne;
        })
        .collect(Collectors.toList());
    }
    etablissement.setPersonnes(etabPersonnes);
    etablissement.setFilieres(getFilieresWithDisciplinesAndUsers(id, etablissement.getSource(), etabPersonnes));

    return new ResponseEntity<>(etablissement, HttpStatus.OK);
  }

  private List<TypeFonctionFiliereDto> getFilieresWithDisciplinesAndUsers(
    Long structureId, String source, List<SimplePersonneDto> personnes
  ) {
    List<FonctionDto> fonctions = fonctionRepository.findByStructureId(structureId);
    if (fonctions.isEmpty()) return Collections.emptyList();

    List<TypeFonctionFiliereDto> typesFonctionFiliere = typeFonctionFiliereRepository.findBySourceSarapis(source);
    List<DisciplineDto> disciplines = disciplineRepository.findBySourceSarapis(source);

    if (log.isDebugEnabled())
      log.debug(
        "<==\n\t- structure : {}\n\t- filieres : {}\n\t- disciplines : {}\n\t- personnes : {}\n\t- source : {}\n\t- filieres of source : {}\n\t- disciplines of source : {}\n==>",
        structureId,
        fonctions.stream().map(FonctionDto::getFiliere).collect(Collectors.toSet()),
        fonctions.stream().map(FonctionDto::getDisciplinePoste).collect(Collectors.toSet()),
        fonctions.stream().map(FonctionDto::getPersonne).collect(Collectors.toSet()),
        source.startsWith(SARAPISUI_) ? source : source + " AND " + SARAPISUI_ + source,
        typesFonctionFiliere.stream().map(TypeFonctionFiliereDto::getId).collect(Collectors.toSet()),
        disciplines.stream().map(DisciplineDto::getId).collect(Collectors.toSet())
      );

    return typesFonctionFiliere.stream()
      // Ajout des disciplines aux filières
      .map(typeFonctionFiliere -> {
        // Filtre les fonctions de la filière
        List<FonctionDto> fonctionsInFiliere = fonctions.stream()
          .filter(fonction -> Objects.equals(fonction.getFiliere(), typeFonctionFiliere.getId()))
          .collect(Collectors.toList());
        // Liste les ID de disciplines de la filière
        Set<Long> disciplineIds = fonctionsInFiliere.stream()
          .map(FonctionDto::getDisciplinePoste)
          .collect(Collectors.toSet());
        // Liste les disciplines de la filière
        List<DisciplineDto> disciplinesInFiliere = disciplines.stream()
          .filter(discipline -> disciplineIds.contains(discipline.getId()) && !Objects.equals(discipline.getDisciplinePoste(), SANS_OBJET))
          .collect(Collectors.toList());
        // Ajout des personnes aux disciplines
        disciplinesInFiliere = disciplinesInFiliere.stream()
          .map(discipline -> {
            // Liste des ID de personne de la discipline
            Set<Long> personneIds = fonctionsInFiliere.stream()
              .filter(fonction -> Objects.equals(fonction.getDisciplinePoste(), discipline.getId()))
              .map(FonctionDto::getPersonne)
              .collect(Collectors.toSet());
            // Liste les personnes de la discipline
            List<SimplePersonneDto> personnesInDiscipline = personnes.stream()
              .filter(personne -> personneIds.contains(personne.getId()))
              .collect(Collectors.toList());
            discipline.setPersonnes(personnesInDiscipline);

            return discipline;
          })
          .collect(Collectors.toList());
        typeFonctionFiliere.setDisciplines(disciplinesInFiliere);

        return typeFonctionFiliere;
      })
      // Retrait des filières sans disciplines
      .filter(typeFonctionFiliere -> !typeFonctionFiliere.getDisciplines().isEmpty() && !Objects.equals(typeFonctionFiliere.getLibelleFiliere(), SANS_OBJET))
      .collect(Collectors.toList());
  }

}
