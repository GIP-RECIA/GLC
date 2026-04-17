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
package fr.recia.glc.web.dto.user;

import fr.recia.glc.db.dto.personne.DatabasePersonneDto;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Etat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class PersonneInListDto {

    private Long id;
    private Etat etat;
    private String nom;
    private String prenom;
    private String uid;
    private CategoriePersonne categoriePersonne;
    private String login;
    private String email;
    private Date dateModification;
    private Date dateSuppression;
    private boolean local;

    public PersonneInListDto(DatabasePersonneDto databasePersonneDto) {
        this.id = databasePersonneDto.getId();
        this.etat = databasePersonneDto.getEtat();
        this.uid = databasePersonneDto.getUid();
        this.nom = databasePersonneDto.getSn();
        this.prenom = databasePersonneDto.getGivenName();
        this.uid = databasePersonneDto.getUid();
        this.categoriePersonne = databasePersonneDto.getCategorie();
        this.login = databasePersonneDto.getLogin();
        this.email = databasePersonneDto.getEmail();
        this.dateModification = databasePersonneDto.getDateModification();
        this.dateSuppression = databasePersonneDto.getDateSuppression();
        this.local = databasePersonneDto.getSource().startsWith("SarapisUi_");
    }

}
