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

import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Civilite;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.ForceEtat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

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
//  private String displayName;
  private String email;
  private String givenName;
//  private String numBureau;
  private String patronyme;
//  private Sexe sexe;
  private String sn;
//  private String titre;
  private String uid;
//  private String uuid;
//  private String emailPersonnel;
//  private boolean listeRouge;
//  private ForceEtat forceEtat;
//  private String idEduConnect;
  private String login;
//  private String alias;
//  private Set<String> prenoms;
  private Long structure;
  private List<FonctionDto> fonctions;
  private List<FonctionDto> additionalFonctions;


  public PersonneDto(Long id, Etat etat, Date anneeScolaire, CategoriePersonne categorie, Civilite civilite,
                     String source, String cn, Date dateNaissance, String email, String givenName, String patronyme,
                     String sn, String uid, String login, Long structure
  ) {
    this.id = id;
    this.etat = etat;
    this.anneeScolaire = anneeScolaire;
    this.categorie = categorie;
    this.civilite = civilite;
    this.source = source;
    this.cn = cn;
    this.dateNaissance = dateNaissance;
    this.email = email;
    this.givenName = givenName;
    this.patronyme = patronyme;
    this.sn = sn;
    this.uid = uid;
    this.login = login;
    this.structure = structure;
  }

}
