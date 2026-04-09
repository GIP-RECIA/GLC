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
package fr.recia.glc.db.dto.personne;

import fr.recia.glc.configuration.Constants;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.relation.RelationDto;
import fr.recia.glc.db.dto.structure.SimpleStructureDto;
import fr.recia.glc.db.entities.common.ExternalId;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Civilite;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.ExternalIdSource;
import fr.recia.glc.utils.PersonneUtils;
import fr.recia.glc.web.dto.user.StructureForUserDto;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class PersonneDto {

    private Long id;
    private Etat etat;
    private Date anneeScolaire;
    private CategoriePersonne categorie;
    private Civilite civilite;
    private String source;
    private String cn;
    private Date dateNaissance;
    private String email;
    private String givenName;
    private String patronyme;
    private String sn;
    private String uid;
    private Date dateFin;
    private Date dateSourceModification;
    private String login;
    private Date dateModification;
    private Date dateAcquittement;
    private Date dateSuppression;
    private String photo;
    // TODO : afficher l'id pronote que si la personne est dans le groupe pronote
    private String idPronote;
    private boolean listeRouge;
    // TODO : la structure courante n'est pas dans la base mais que dans le LDAP
    private List<StructureForUserDto> listeStructures;
    // TODO : relations des personnes
    private List<RelationDto> relations;

    public PersonneDto(APersonne aPersonne, boolean showUid) {
        this.id = aPersonne.getId();
        this.etat = aPersonne.getEtat();
        this.anneeScolaire = aPersonne.getAnneeScolaire();
        this.categorie = aPersonne.getCategorie();
        this.civilite = aPersonne.getCivilite();
        this.source = aPersonne.getCleJointure().getSource();
        this.cn = aPersonne.getCn();
        this.dateNaissance = aPersonne.getDateNaissance();
        this.email = aPersonne.getEmail();
        this.givenName = aPersonne.getGivenName();
        this.patronyme = aPersonne.getPatronyme();
        this.sn = aPersonne.getSn();
        if (showUid) {
            this.uid = aPersonne.getUid();
        }
        this.login = aPersonne.getLogin().getNom();
        this.dateFin = aPersonne.getDateFin();
        this.dateSourceModification = aPersonne.getDateSourceModification();
        if (aPersonne.getEtat() == Etat.Delete && aPersonne.getDateModification().equals(aPersonne.getDateAcquittement())) {
            this.etat = Etat.Deleting;
            this.dateSuppression = aPersonne.getDateModification();
        }
        this.photo = aPersonne.getPhoto();
        for(ExternalId externalId : aPersonne.getExternalIds()){
            if(externalId.getDestinataire().equals(ExternalIdSource.PRONOTE)){
                this.idPronote = externalId.getId();
            }
        }
        this.listeRouge = aPersonne.isListeRouge();
        this.listeStructures = new ArrayList<>();
        this.relations = new ArrayList<>();
    }

    public void setAllFonctions(List<FonctionDto> fonctions) {
        if (!fonctions.isEmpty()) {
            for(FonctionDto fonctionDto : fonctions){
                // Ici c'est acceptable de faire une boucle imbriquée car on a peu de structures dans la liste la pluspart du temps
                for(StructureForUserDto structureForUserDto : this.listeStructures){
                    if(fonctionDto.getStructure().equals(structureForUserDto.getId())){
                        if(!fonctionDto.getSource().startsWith(Constants.SARAPISUI_)){
                            structureForUserDto.getFonctions().add(fonctionDto);
                        } else {
                            structureForUserDto.getAdditionalFonctions().add(fonctionDto);
                        }
                    }
                }
            }
        }
    }

}
