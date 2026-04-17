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
package fr.recia.glc.db.dto.structure;

import fr.recia.glc.db.dto.AlertDto;
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.dto.personne.DatabasePersonneDto;
import fr.recia.glc.db.entities.common.Adresse;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.entities.structure.ServiceAcademique;
import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.EtatAlim;
import fr.recia.glc.web.dto.user.CardPersonneDto;
import fr.recia.glc.web.dto.user.PersonneInListDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class StructureDto {

    private Long id;
    private String uai;
    private Etat etat;
    private EtatAlim etatAlim;
    private String source;
    private Date anneeScolaire;
    private Adresse adresse;
    private CategorieStructure categorie;
    private String type;
    private String mail;
    private String nom;
    private String nomCourt;
    private String siren;
    private String siteWeb;
    private String modeleLogin;
    private String logo;

    private List<TypeFonctionFiliereDto> filieres;
    private List<PersonneInListDto> personnes;
    private List<DatabasePersonneDto> withoutFunctions;
    private List<AlertDto> alerts;
    private String permission;

    public StructureDto(AStructure aStructure) {
        this.id = aStructure.getId();
        if(aStructure.getCategorie().equals(CategorieStructure.Etablissement)){
            this.uai = ((Etablissement) aStructure).getUai();
        }
        else if(aStructure.getCategorie().equals(CategorieStructure.Service_academique)){
            this.uai = ((ServiceAcademique) aStructure).getUai();
        }
        this.etat = aStructure.getEtat();
        this.etatAlim = aStructure.getEtatAlim();
        this.source = aStructure.getCleJointure().getSource();
        this.anneeScolaire = aStructure.getAnneeScolaire();
        this.adresse = aStructure.getAdresse();
        this.categorie = aStructure.getCategorie();
        this.mail = aStructure.getMail();
        this.nomCourt = aStructure.getNomCourt();
        this.siren = aStructure.getSiren();
        this.siteWeb = aStructure.getSiteWeb();
        this.modeleLogin = aStructure.getModeleLogin();
        this.logo = aStructure.getLogo();

        String[] split = aStructure.getNom().split("\\$");
        if (split.length > 1) {
            this.type = split[0];
            this.nom = split[1];
        } else {
            this.nom = aStructure.getNom();
        }
    }

    public void setListePersonnes(List<DatabasePersonneDto> personnes){
        this.personnes = new ArrayList<>();
        for(DatabasePersonneDto personneDto : personnes){
            this.personnes.add(new PersonneInListDto(personneDto));
        }
    }

    public void setComposition(List<FonctionDto> fonctions,
                            Map<Long, TypeFonctionFiliereDto> typesFonctionFiliere,
                            Map<Long, DisciplineDto> disciplines,
                            Map<Long, DatabasePersonneDto> personnesMap) {

        // Si pas de fonctions dans l'établissement on retourne une map vide
        if (fonctions.isEmpty()) {
            setFilieres(List.of());
            return;
        }

        // Maps indexées qu'on garde à côté pour pouvoir accéder facilement aux objets
        Map<Long, TypeFonctionFiliereDto> filieresMap = new HashMap<>();
        Map<Long, Map<Long, DisciplineDto>> disciplinesMap = new HashMap<>();

        // Liste finale qu'on va retourner au front
        List<TypeFonctionFiliereDto> filieresWithDisciplines = new ArrayList<>();

        for (FonctionDto fonctionDto : fonctions) {
            // Ajout de la filière si elle n'existe pas
            Long filiereId = fonctionDto.getFiliere();
            TypeFonctionFiliereDto typeFonctionFiliereDto;
            if (!filieresMap.containsKey(filiereId)) {
                typeFonctionFiliereDto = new TypeFonctionFiliereDto(typesFonctionFiliere.get(filiereId));
                filieresMap.put(filiereId, typeFonctionFiliereDto);
                disciplinesMap.put(filiereId, new HashMap<>());
                filieresWithDisciplines.add(typeFonctionFiliereDto);
            } else {
                typeFonctionFiliereDto = filieresMap.get(filiereId);
            }

            // Ajout de la discipline si elle n'existe pas
            Long disciplineId = fonctionDto.getDiscipline();
            // Cas spécial pour les CFA
            if (disciplineId == null) {
                DatabasePersonneDto databasePersonneDto = personnesMap.get(fonctionDto.getPersonne());
                typeFonctionFiliereDto.getPersonnesWithoutDiscipline().add(databasePersonneDto);
            } else {
                if (disciplines.containsKey(disciplineId)) {
                    DisciplineDto disciplineDto;
                    if (!disciplinesMap.get(filiereId).containsKey(disciplineId)) {
                        disciplineDto = new DisciplineDto(disciplines.get(disciplineId));
                        disciplinesMap.get(filiereId).put(disciplineId, disciplineDto);
                        typeFonctionFiliereDto.getDisciplines().add(disciplineDto);
                    } else {
                        disciplineDto = disciplinesMap.get(filiereId).get(disciplineId);
                    }
                    // Ajout de la personne dans la discipline
                    if (personnesMap.containsKey(fonctionDto.getPersonne())) {
                        DatabasePersonneDto databasePersonneDto = personnesMap.get(fonctionDto.getPersonne());
                        disciplineDto.getPersonnes().add(new CardPersonneDto(databasePersonneDto));
                    } else {
                        log.warn("person in functions by not in structure : {}", fonctionDto.getPersonne());
                    }
                } else {
                    log.warn("discipline {} is not in known disciplines for {}", disciplineId, fonctionDto.getSource());
                }
            }
        }

        setFilieres(filieresWithDisciplines);
    }

}
