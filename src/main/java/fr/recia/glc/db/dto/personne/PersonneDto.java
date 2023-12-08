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
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Civilite;
import fr.recia.glc.db.enums.Etat;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
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
  private Date dateFin;
  private Date dateSourceModification;
  private String login;
//  private String alias;
//  private Set<String> prenoms;
  private Long structure;
  private List<FonctionDto> fonctions;
  private List<FonctionDto> additionalFonctions;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant dateModification;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant dateAcquittement;

  public PersonneDto(
    Long id, Etat etat, Date anneeScolaire, CategoriePersonne categorie, Civilite civilite, String source, String cn,
    Date dateNaissance, String email, String givenName, String patronyme, String sn, String uid, String login,
    Long structure, Date dateFin, Date dateSourceModification, Instant dateModification, Instant dateAcquittement
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
    this.dateFin = dateFin;
    this.dateSourceModification = dateSourceModification;

    if (etat == Etat.Delete && dateModification.equals(dateAcquittement))
      this.etat = Etat.Deleting;
  }

  public PersonneDto(APersonne aPersonne) {
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
    this.uid = aPersonne.getUid();
    this.login = aPersonne.getLogin().getNom();
//    this.structure = aPersonne.getStructRattachement().getId(); // TODO
    this.dateFin = aPersonne.getDateFin();
    this.dateSourceModification = aPersonne.getDateSourceModification();

    if (aPersonne.getEtat() == Etat.Delete && aPersonne.getDateModification().equals(aPersonne.getDateAcquittement()))
      this.etat = Etat.Deleting;
  }

  public void setAllFonctions(List<FonctionDto> fonctions) {
    if (!fonctions.isEmpty()) {
      setFonctions(fonctions.stream().filter(fonction -> !fonction.getSource().startsWith(Constants.SARAPISUI_)).collect(Collectors.toList()));
      setAdditionalFonctions(fonctions.stream().filter(fonction -> fonction.getSource().startsWith(Constants.SARAPISUI_)).collect(Collectors.toList()));
    }
  }

}
