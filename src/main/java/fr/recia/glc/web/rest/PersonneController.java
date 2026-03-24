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
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.repositories.structure.EtablissementRepository;
import fr.recia.glc.security.GLCRole;
import fr.recia.glc.security.GLCUser;
import fr.recia.glc.services.db.AddPersonneService;
import fr.recia.glc.services.db.EtablissementService;
import fr.recia.glc.web.dto.function.JsonAdditionalFonctionBody;
import fr.recia.glc.web.dto.function.JsonAdditionalFonctionOldBody;
import fr.recia.glc.services.db.FonctionService;
import fr.recia.glc.services.db.PersonneService;
import fr.recia.glc.web.dto.user.UserCreation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
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
    private EtablissementRepository<Etablissement> etablissementRepository;

    @GetMapping
    public ResponseEntity<List<SimplePersonneDto>> searchPersonne(@RequestParam(value = "name") String name) {
        List<SimplePersonneDto> personnes = personneService.searchPersonne(name, true);
        if (personnes.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(personnes, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PersonneDto> getPersonne(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id) {
        APersonne personne = personneService.getPersonne(id);
        if (personne == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Vérifier qu'on a les droits de voir la personne = que sur une des structures dans laquelle est la personne on a les droits de visualisation
        Set<String> allowedUAI = principal.getRightsForEtabs().get(GLCRole.READ);
        boolean canRead = false;
        // ok pour cette boucle for car quand la personne est cachée on ne va pas recharger la liste des structures dans la base
        for(AStructure aStructure : personne.getListeStructures()){
            // TODO : plus propre pour la récupération par UAI -> gérer le cas des collectivités
            if(aStructure instanceof Etablissement){
                if(allowedUAI.contains(((Etablissement) aStructure).getUai())){
                    canRead = true;
                    break;
                }
            }
        }
        if(canRead){
            PersonneDto personneDto = new PersonneDto(personne);
            personneDto.setAllFonctions(fonctionService.getPersonneFonctions(id));
            return new ResponseEntity<>(personneDto, HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to view person {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

    }

    @PostMapping
    public ResponseEntity<Void> addPersonne(@AuthenticationPrincipal GLCUser principal, @RequestBody UserCreation userCreation) {
        // Vérifier qu'on a les droits d'ajouter la personne = que sur la structure sur laquelle on veut l'ajouter on a les droits d'écriture
        Etablissement etablissement = etablissementRepository.findById(userCreation.getStructureRattachement()).orElseThrow();
        Set<String> allowedUAI = principal.getRightsForEtabs().get(GLCRole.READ);
        // TODO : gérer la partie des collectivités
        // TODO : cache sur l'établissement pour éviter de refaire une nouvelle requête en BD à chaque fois
        if(allowedUAI.contains(etablissement.getUai())){
            addPersonneService.addPersonne(userCreation);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            log.warn("User {} is not authorized to add person in {}", principal.getUsername(), userCreation.getStructureRattachement());
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PostMapping(value = "/{id}/fonction")
    public ResponseEntity<Void> setPersonneAdditionalFonctions(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id, @RequestBody JsonAdditionalFonctionOldBody body) {
        // Vérifier qu'on a les droits de modifier la personne = que sur la structure dans laquelle on veut modifier la fonction on a les droits d'écriture
        Etablissement etablissement = etablissementRepository.findById(body.getStructureId()).orElseThrow();
        Set<String> allowedUAI = principal.getRightsForEtabs().get(GLCRole.READ);
        // TODO : gérer la partie des collectivités
        // TODO : cache sur l'établissement pour éviter de refaire une nouvelle requête en BD à chaque fois
        if(allowedUAI.contains(etablissement.getUai())){
            boolean success = fonctionService.saveAdditionalFonctions(
                    id,
                    body.getStructureId(),
                    body.getToAddFunctions(),
                    body.getToDeleteFunctions(),
                    body.getRequiredAction()
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
