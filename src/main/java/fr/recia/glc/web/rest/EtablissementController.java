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

import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.dto.structure.EtablissementDto;
import fr.recia.glc.db.dto.structure.SimpleStructureDto;
import fr.recia.glc.security.GLCRole;
import fr.recia.glc.security.GLCUser;
import fr.recia.glc.services.alert.AlertService;
import fr.recia.glc.services.db.CollectiviteService;
import fr.recia.glc.services.db.EtablissementService;
import fr.recia.glc.services.db.FonctionService;
import fr.recia.glc.services.db.PersonneService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping(value = "/api/structure/etablissement")
public class EtablissementController {

    @Autowired
    private EtablissementService etablissementService;
    @Autowired
    private CollectiviteService collectiviteService;
    @Autowired
    private FonctionService fonctionService;
    @Autowired
    private PersonneService personneService;
    @Autowired
    private AlertService alertService;

    /**
     * Récupère la liste de toutes les structures que l'utilisateur à le droit d'administrer
     */
    @GetMapping()
    public ResponseEntity<List<SimpleStructureDto>> getEtablissements(@AuthenticationPrincipal GLCUser principal) {
        // Ne retourner que les établissements que la personne a le droit de lire
        Set<String> allowedUAI = principal.getRightsForEtabs().get(GLCRole.READ);
        List<SimpleStructureDto> etablissements = etablissementService.getEtablissements(allowedUAI);
        // Gestion des collectivités à côté pour l'instant pour simplifier la gestion des droits
        if(principal.getRightsForColl().contains(GLCRole.READ)){
            etablissements.addAll(collectiviteService.getCollectivites());
        }
        if (etablissements.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(etablissements, HttpStatus.OK);
    }

    /**
     * Récupère les informations sur un établissement ainsi que toutes les personnes dedans
     */
    @GetMapping(value = "/{id}")
    // TODO : une autre route spécifique pour les collectivités
    public ResponseEntity<EtablissementDto> getEtablissement(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id) {
        EtablissementDto etablissement = etablissementService.getEtablissement(id);
        if (etablissement == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        // Check si la personne à le droit de lire sur l'établissement
        Set<String> allowedUAI = principal.getRightsForEtabs().get(GLCRole.READ);
        if (!allowedUAI.contains(etablissement.getUai())) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        // Check si la personne à le droit d'écrire sur l'établissement
        if(principal.getRightsForEtabs().get(GLCRole.WRITE).contains(etablissement.getUai())){
            etablissement.setPermission("ADMIN");
        }

        // Récupération de la liste des personnes
        List<SimplePersonneDto> etabPersonnes = personneService.getPersonnes(id);
        etablissement.setPersonnes(etabPersonnes);

        // Récupération de la liste des personnes sans fonction
        List<SimplePersonneDto> withoutFunction = fonctionService.getPersonnesWithoutFunctions(id);
        etablissement.setWithoutFunctions(withoutFunction);

        // Récupération des alertes
        etablissement.setAlerts(alertService.getFonctionAlerts(etablissement.getId(), etablissement.getSource()));

        // Récupération des filières (fonctions, typesFonctionFiliere et disciplines)
        List<FonctionDto> fonctions = fonctionService.getStructureFonctions(id);
        // On créé des maps pour pouvoir récupérer les objets par leur id en O(1)
        List<TypeFonctionFiliereDto> typesFonctionFiliereList = fonctionService.getTypesFonctionFiliere(etablissement.getSource());
        Map<Long, TypeFonctionFiliereDto> typesFonctionFiliereMap = typesFonctionFiliereList.stream()
            .collect(Collectors.toMap(
                    TypeFonctionFiliereDto::getId,
                    Function.identity()
            ));
        List<DisciplineDto> disciplinesList = fonctionService.getDisciplines(etablissement.getSource());
        Map<Long, DisciplineDto> disciplinesMap = disciplinesList.stream()
            .collect(Collectors.toMap(
                    DisciplineDto::getId,
                    Function.identity()
            ));
        Map<Long, SimplePersonneDto> personnesMap = etabPersonnes.stream()
                .collect(Collectors.toMap(
                        SimplePersonneDto::getId,
                        Function.identity()
                ));

        etablissement.setFilieres(fonctions, typesFonctionFiliereMap, disciplinesMap, personnesMap);

        return new ResponseEntity<>(etablissement, HttpStatus.OK);
    }

}
