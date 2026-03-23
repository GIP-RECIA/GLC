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
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.common.Adresse;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.EtatAlim;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class EtablissementDto {

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
    private List<SimplePersonneDto> personnes;
    private List<SimplePersonneDto> withoutFunctions;
    private List<AlertDto> alerts;
    private String permission;

    public EtablissementDto(Etablissement etablissement) {
        this.id = etablissement.getId();
        this.uai = etablissement.getUai();
        this.etat = etablissement.getEtat();
        this.etatAlim = etablissement.getEtatAlim();
        this.source = etablissement.getCleJointure().getSource();
        this.anneeScolaire = etablissement.getAnneeScolaire();
        this.adresse = etablissement.getAdresse();
        this.categorie = etablissement.getCategorie();
        this.mail = etablissement.getMail();
        this.nomCourt = etablissement.getNomCourt();
        this.siren = etablissement.getSiren();
        this.siteWeb = etablissement.getSiteWeb();
        this.modeleLogin = etablissement.getModeleLogin();
        this.logo = etablissement.getLogo();

        String[] split = etablissement.getNom().split("\\$");
        if (split.length > 1) {
            this.type = split[0];
            this.nom = split[1];
        } else {
            this.nom = etablissement.getNom();
        }
    }

    public void setFilieres(List<FonctionDto> fonctions,
                            Map<Long, TypeFonctionFiliereDto> typesFonctionFiliere,
                            Map<Long, DisciplineDto> disciplines,
                            Map<Long, SimplePersonneDto> personnesMap) {

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

        for(FonctionDto fonctionDto : fonctions){
            // Ajout de la filière si elle n'existe pas
            Long filiereId = fonctionDto.getFiliere();
            TypeFonctionFiliereDto typeFonctionFiliereDto;
            if(!filieresMap.containsKey(filiereId)){
                typeFonctionFiliereDto = new TypeFonctionFiliereDto(typesFonctionFiliere.get(filiereId));
                filieresMap.put(filiereId, typeFonctionFiliereDto);
                disciplinesMap.put(filiereId, new HashMap<>());
                filieresWithDisciplines.add(typeFonctionFiliereDto);
            } else {
                typeFonctionFiliereDto = filieresMap.get(filiereId);
            }

            // Ajout de la discipline si elle n'existe pas
            Long disciplineId = fonctionDto.getDiscipline();
            DisciplineDto disciplineDto;
            if(!disciplinesMap.get(filiereId).containsKey(disciplineId)){
                disciplineDto = new DisciplineDto(disciplines.get(disciplineId));
                disciplinesMap.get(filiereId).put(disciplineId, disciplineDto);
                typeFonctionFiliereDto.getDisciplines().add(disciplineDto);
            } else {
                disciplineDto = disciplinesMap.get(filiereId).get(disciplineId);
            }
            // Ajout de la personne dans la discipline
            if(personnesMap.containsKey(fonctionDto.getPersonne())) {
                SimplePersonneDto simplePersonneDto = personnesMap.get(fonctionDto.getPersonne());
                disciplineDto.getPersonnes().add(simplePersonneDto);
            } else {
                log.warn("person in functions by not in structure : {}", fonctionDto.getPersonne());
            }
        }

        setFilieres(filieresWithDisciplines);
    }

}
