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

import fr.recia.glc.audit.AuditEvent;
import fr.recia.glc.audit.AuditService;
import fr.recia.glc.audit.EventType;
import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.db.repositories.structure.EtablissementRepository;
import fr.recia.glc.security.GLCRole;
import fr.recia.glc.security.GLCUser;
import fr.recia.glc.services.cache.CacheInvalidationService;
import fr.recia.glc.services.db.AddPersonneService;
import fr.recia.glc.services.db.FonctionService;
import fr.recia.glc.services.db.GroupeService;
import fr.recia.glc.services.db.PersonneService;
import fr.recia.glc.services.db.RelationService;
import fr.recia.glc.web.dto.function.JsonAdditionalFonctionBody;
import fr.recia.glc.web.dto.user.StructureForUserDto;
import fr.recia.glc.web.dto.user.UserCreation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping(value = "/api/personne")
public class PersonneController {

    @Autowired
    private FonctionService fonctionService;
    @Autowired
    private PersonneService personneService;
    @Autowired
    private AddPersonneService addPersonneService;
    @Autowired
    private GroupeService groupeService;
    @Autowired
    private RelationService relationService;
    @Autowired
    private EtablissementRepository<Etablissement> etablissementRepository;
    @Autowired
    private AStructureRepository<AStructure> aStructureRepository;
    @Autowired
    private AuditService auditService;

    @GetMapping

    public ResponseEntity<List<SimplePersonneDto>> searchPersonne(@AuthenticationPrincipal GLCUser principal,
                                                                  @RequestParam(value = "name") String name,
                                                                  @RequestParam(value = "etab", required = false) Long etabId,
                                                                  @RequestParam(value = "not_in_etab", required = false) Long notInEtabId,
                                                                  @RequestParam(value = "staff", required = false, defaultValue = "False") boolean staff,
                                                                  @RequestParam(value = "check_rights", required = false, defaultValue = "True") boolean checkRights) {
        List<SimplePersonneDto> personnes;
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.READ);
        // TODO : vérification de droits plus propre pour autoriser les admins à chercher par uid
        boolean canSearchByUid = !principal.getRightsForEtabs().get(GLCRole.VIEW_UID).isEmpty();

        // Cas de la recherche dans un établissement
        if(etabId != null){
            AStructure aStructure = aStructureRepository.getReferenceById(etabId);
            if(allowedSiren.contains(aStructure.getSiren())){
                // Ici pas besoin de revérifier les droits car ils sont déjà véirifiés implicitement comme on recherche uniquement dans l'établissement
                if(staff){
                    personnes = personneService.searchPersonneInEtabInStaffCategories(name, Set.of(etabId), canSearchByUid);
                } else {
                    personnes = personneService.searchPersonneInEtab(name, Set.of(etabId), canSearchByUid);
                }
            } else {
                log.warn("User {} is not authorized to view etab {}", principal.getUsername(), etabId);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        // Cas de la recherche ou on évite un établissement
        else if(notInEtabId != null){
            // recherche hors d'un établissement sans vérifier les droits = recherche utilisée pour le rattachement
            if(!checkRights){
                if(staff){
                    personnes = personneService.searchPersonneNotInEtabInStaffCategories(name, Set.of(notInEtabId), canSearchByUid);
                } else {
                    personnes = personneService.searchPersonneNotInEtab(name, Set.of(notInEtabId), canSearchByUid);
                }
            } else {
                if(staff){
                    personnes = personneService.searchPersonneNotInEtabButInSirenInStaffCategories(name, Set.of(notInEtabId), allowedSiren, canSearchByUid);
                } else {
                    personnes = personneService.searchPersonneNotInEtabButInSiren(name, Set.of(notInEtabId), allowedSiren, canSearchByUid);
                }
            }
        }

        // Cas de la recherche globale (pas dans un établissement, et on évite pas non plus un établissement)
        else {
            if(!checkRights){
                if(staff){
                    personnes = personneService.searchPersonneInStaffCategories(name, canSearchByUid);
                } else {
                    personnes = personneService.searchPersonne(name, canSearchByUid);
                }
            } else {
                // On ne cherche que les personnes dans les sirens autorisés
                if(staff){
                    personnes = personneService.searchPersonneInEtabBySirenInStaffCategories(name, allowedSiren, canSearchByUid);
                } else {
                    personnes = personneService.searchPersonneInEtabBySiren(name, allowedSiren, canSearchByUid);
                }
            }
        }

        return new ResponseEntity<>(personnes, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonneDto> getPersonne(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id) {
        // TODO : la route est aussi appellée lors du rattachement sauf qu'on a pas forcément les droits sur l'étab duquel la personne est de base
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de voir la personne = que sur une des structures dans laquelle est la personne on a les droits de visualisation
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.READ);
        boolean canRead = false;
        boolean showUid = false;
        // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canRead = true;
            }
            if (principal.getRightsForEtabs().get(GLCRole.VIEW_UID).contains(aStructure.getSiren())) {
                showUid = true;
            }
        }
        if (canRead) {
            // Booléen qui indique si on affiche l'uid ou non
            PersonneDto personneDto = new PersonneDto(personne, showUid);
            for(AStructure aStructure : personne.getListeStructures()){
                StructureForUserDto structureForUserDto = new StructureForUserDto(aStructure);
                personneDto.getListeStructures().add(structureForUserDto);
                if(aStructure.getId()==personne.getStructRattachement().getId()){
                    structureForUserDto.setStructureRattachement(true);
                }
                structureForUserDto.setClasses(groupeService.getClassesOfPersonne(personne.getId(), personne.getCategorie(), aStructure.getId()));
                structureForUserDto.setGroupesPedagogiques(groupeService.getGroupesOfPersonne(personne.getId(), personne.getCategorie(), aStructure.getId()));
            }
            personneDto.setAllFonctions(fonctionService.getPersonneFonctions(id));
            personneDto.setRelations(relationService.getPersonneRelations(id));
            return new ResponseEntity<>(personneDto, HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to view person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}/lock")
    public ResponseEntity<Void> lockPerson(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id){
        // TODO : a-t-on besoin de modifier la date de modification de la personne ? à vérifier
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        boolean canRead = false;
        // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canRead = true;
            }
        }
        if (canRead) {
            personneService.lockPerson(personne);
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.LOCK_ACCOUNT)
                    .actor(principal.getUsername())
                    .target(String.valueOf(id))
                    .payload(Map.of(
                        "uid", personne.getUid()
                    ))
                    .build()
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to lock person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}/unlock")
    public ResponseEntity<Void> unlockPerson(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id){
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        boolean canRead = false;
        // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canRead = true;
            }
        }
        if (canRead) {
            personneService.unlockPerson(personne);
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.UNLOCK_ACCOUNT)
                    .actor(principal.getUsername())
                    .target(String.valueOf(id))
                    .payload(Map.of(
                        "uid", personne.getUid()
                    ))
                    .build()
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to unlock person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/unlock")
    public ResponseEntity<Void> unlockPersons(@AuthenticationPrincipal GLCUser principal, @RequestParam List<Long> ids){
        // TODO : débloquage de masse en une seule requête pour être plus efficace
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        for(Long id : ids){
            APersonne personne = personneService.getPersonne(id);
            if (personne == null) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
            // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
            boolean canRead = false;
            // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
            for (AStructure aStructure : personne.getListeStructures()) {
                if (allowedSiren.contains(aStructure.getSiren())) {
                    canRead = true;
                }
            }
            if (canRead) {
                personneService.unlockPerson(personne);
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.UNLOCK_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
            } else {
                log.warn("User {} is not authorized to unlock person {}", principal.getUsername(), id);
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> putInDeleteState(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id){
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        boolean canModify = false;
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canModify = true;
            }
        }
        if (canModify) {
            boolean ok = personneService.putInDeleteState(personne);
            if (ok) {
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.DELETE_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to put person {} in delete state", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}/force")
    public ResponseEntity<Void> forceDelete(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id) {
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        boolean canModify = false;
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canModify = true;
            }
        }
        if (canModify) {
            boolean ok = personneService.forceDelete(personne);
            if (ok) {
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.FORCEDELETE_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to force delete person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}/undo")
    public ResponseEntity<Void> undoDelete(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id){
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de modifier la personne = que sur une des structures dans laquelle est la personne on a les droits de modification
        boolean canModify = false;
        for (AStructure aStructure : personne.getListeStructures()) {
            if (allowedSiren.contains(aStructure.getSiren())) {
                canModify = true;
            }
        }
        if (canModify) {
            boolean ok = personneService.undoDelete(personne);
            if(ok){
                // Log Audit
                auditService.log(
                    AuditEvent.builder()
                        .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                        .eventType(EventType.UNDODELETE_ACCOUNT)
                        .actor(principal.getUsername())
                        .target(String.valueOf(id))
                        .payload(Map.of(
                            "uid", personne.getUid()
                        ))
                        .build()
                );
                return new ResponseEntity<>(HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            log.warn("User {} is not authorized to undo delete for person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping
    public ResponseEntity<Void> addPersonne(@AuthenticationPrincipal GLCUser principal, @RequestBody UserCreation userCreation) {
        // Vérifier qu'on a les droits d'ajouter la personne = que sur la structure sur laquelle on veut l'ajouter on a les droits d'écriture
        Etablissement etablissement = etablissementRepository.findById(userCreation.getStructureRattachement()).orElseThrow();
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        // TODO : gérer la partie des collectivités
        // TODO : cache sur l'établissement pour éviter de refaire une nouvelle requête en BD à chaque fois
        if (allowedSiren.contains(etablissement.getSiren())) {
            APersonne apersonne = addPersonneService.addPersonne(userCreation);
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.CREATE_ACCOUNT)
                    .actor(principal.getUsername())
                    .target(String.valueOf(apersonne.getId()))
                    .payload(Map.of(
                        "uid", apersonne.getUid()
                    ))
                    .build()
            );
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to add person in {}", principal.getUsername(), userCreation.getStructureRattachement());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/{id}/fonction")
    public ResponseEntity<Void> setPersonneAdditionalFonctions(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id, @RequestBody JsonAdditionalFonctionBody body) {
        // Vérifier qu'on a les droits de modifier la personne = que sur la structure dans laquelle on veut modifier la fonction on a les droits d'écriture
        Etablissement etablissement = etablissementRepository.findById(body.getStructureId()).orElseThrow();
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        // TODO : gérer la partie des collectivités
        // TODO : cache sur l'établissement pour éviter de refaire une nouvelle requête en BD à chaque fois
        if (allowedSiren.contains(etablissement.getSiren())) {
            boolean success = fonctionService.saveAdditionalFonctions(
                id,
                body.getStructureId(),
                body.getToAddFunctions(),
                body.getToDeleteFunctions(),
                body.getRequiredAction()
            );
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.MODIFY_FONCTION)
                    .actor(principal.getUsername())
                    .target(id.toString())
                    .payload(Map.of(
                        "body", body,
                        "success", success)
                    )
                    .build()
            );
            if (!success) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to modify funtion for user {} in {}", principal.getUsername(), id, body.getStructureId());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
