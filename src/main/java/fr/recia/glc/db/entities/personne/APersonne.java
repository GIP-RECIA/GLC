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
package fr.recia.glc.db.entities.personne;

import fr.recia.glc.db.entities.common.AbstractEntity;
import fr.recia.glc.db.entities.common.Adresse;
import fr.recia.glc.db.entities.common.CleJointure;
import fr.recia.glc.db.entities.common.ExternalId;
import fr.recia.glc.db.entities.common.Mail;
import fr.recia.glc.db.entities.common.Telephone;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Civilite;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.ForceEtat;
import fr.recia.glc.db.enums.Sexe;
import fr.recia.glc.db.entities.fonction.AFonction;
import fr.recia.glc.db.entities.gestion.Incertain;
import fr.recia.glc.db.entities.groupe.MappingAGroupeAPersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.UniqueConstraint;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "apersonne", indexes = {
  @Index(name = "APersCleJointure", columnList = "cle, source"),
  @Index(name = "uid_index", columnList = "uid"),
  @Index(name = "uuid_index", columnList = "uuid")
}, uniqueConstraints = {
  @UniqueConstraint(columnNames = {"cle", "source"})
})
@Getter
@Setter
public abstract class APersonne extends AbstractEntity {

  /**
   * Etat de l'entité valide, bloqué ou supprimé.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I25)
  private Etat etat;
  /**
   * Année scolaire de validité de l'individu. Année à la rentrée de septembre.
   */
  @Temporal(TemporalType.DATE)
  private Date anneeScolaire;
  /**
   * Catégorie de la personne : Eleve, Enseignant ...
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I40)
  private CategoriePersonne categorie;
  /**
   * Adresse de la personne.
   */
  @Embedded
  private Adresse adresse;
  /**
   * Civilité : M, Mme, Mlle.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I4)
  private Civilite civilite;
  /**
   * Clé de jointure, identifiant unique généré par les différentes sources,
   * mais unique uniquement pour le périmètre de la source.
   */
  @Embedded
  @Column(unique = true, nullable = false)
  private CleJointure cleJointure;
  /**
   * Nom canonique de la personne : NOM + Prénom usuels.
   */
  private String cn;
  /**
   * Date de naissance.
   */
  @Temporal(TemporalType.DATE)
  private Date dateNaissance;
  /**
   * Nom et prénom accentué.
   */
  private String displayName;
  /**
   * Adresse e-mail.
   */
  private String email;
  /**
   * Prénom usuel.
   */
  private String givenName;
  /**
   * Numéro de bureau.
   */
  @Column(length = IntConst.I40)
  private String numBureau;
  /**
   * Password généré.
   */
  private String password;
  /**
   * Password SambaNT.
   */
  private String sambaNTPassword;
  /**
   * Nom patronymique.
   */
  private String patronyme;
  /**
   * Photo de l'individu, décrit par un url.
   */
  private String photo;
  /**
   * Sexe de la personne : F, M.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = 1, columnDefinition = "CHAR")
  private Sexe sexe;
  /**
   * Nom d'usage.
   */
  private String sn;
  /**
   * Titre de la personne.
   */
  @Column(length = IntConst.I80)
  private String titre;
  /**
   * Identifiant interne à l'ENT.
   */
  @Column(unique = true, length = IntConst.I8)
  // INDEX
  private String uid;
  /**
   * Identifiant externe GARPersonIdentifiant.
   */
  @Column(unique = true, nullable = false, length = IntConst.I36)
  // INDEX
  private String uuid;
  // ajout spécifique à la gestion des comptes
  /**
   * Adresse e-mail personnelle.
   */
  private String emailPersonnel;
  /**
   * Flag pour indiquer la préférence du mail personnel. Par défaut à false.
   */
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
  private boolean doForward;
  /**
   * Sauvegarde de la date de validation de la charte.
   */
  @Temporal(TemporalType.TIMESTAMP)
  private Date validationCharte;
  /**
   * Flag pour indiquer que le compte est placé en liste rouge. Par défaut à false.
   */
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
  private boolean listeRouge;
  /**
   * Pour forcer l'état d'un compte : ex suppression avant les 20 jours ou réactivation d'un compte supprimé (à voir).
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I25)
  private ForceEtat forceEtat;
  /**
   * Clé EduConnect pour forcer l'association.
   */
  @Column(length = IntConst.I100)
  private String idEduConnect;

  /**
   * Relation bidirectionnelle.
   * Login de l'individu généré par l'ENT.
   */
  @OneToOne(fetch = FetchType.LAZY, mappedBy = "apersonneLogin")
  private Login login;
  /**
   * Relation bidirectionnelle.
   * Alias remplaçant le login, personnalisé par l'individu.
   */
  @OneToOne(fetch = FetchType.LAZY, mappedBy = "apersonneAlias")
  private Login alias;
  /**
   * Relation bidirectionnelle.
   * Liste des anciens alias de l'individu qui ne sont plus utilisés.
   */
  @OneToMany(fetch = FetchType.LAZY, mappedBy = "apersonneOldAlias")
  private Collection<Login> oldAlias;
  /**
   * Relation bidirectionnelle.
   * Liste des fonctions d'une personne, uniquement les fonctions dans une structure, pas les fonctions de groupes.
   * Profils, classes, groupes, role applicatif
   */
  @OneToMany(mappedBy = "personne", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  private Set<AFonction> fonctions = new HashSet<>();
  /**
   * Relation bidirectionnelle.
   */
  @OneToMany(mappedBy = "pk.personne", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  private Set<MappingAGroupeAPersonne> groupes = new HashSet<>();
  /**
   * Relation avec type primitif.
   * Liste des prénoms.
   */
  @ElementCollection
  @Column(length = 70)
  private Set<String> prenoms = new HashSet<>();
  /**
   * Relation unidirectionnelle.
   * Structure à laquelle la personne est rattachée administrativement.
   */
  @Fetch(FetchMode.JOIN)
  @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinColumn(name = "structure_rattachement_fk")
  private AStructure structRattachement;
  /**
   * Relation unidirectionnelle.
   */
  @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinColumn(name = "apersonne_fk")
  private Set<Telephone> telephones = new HashSet<>();
  /**
   * Relation unidirectionnelle.
   * Liste des adresses mails autres.
   */
  @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinColumn(name = "apersonne_fk")
  private Set<Mail> autresMails = new HashSet<>();
  /**
   * Relation bidirectionnelle. Liste des attributs incertains.
   */
  @OneToMany(mappedBy = "incertainPers", cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  private Set<Incertain> incertains = new HashSet<>();
  /**
   * Relation bidirectionnelle.
   * Liste des structures dans lesquelles la personne intervient.
   */
  @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinTable(
    name = "apersonnes_astructures",
    joinColumns = @JoinColumn(name = "APERSONNE_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "ASTRUCTURE_ID", referencedColumnName = "ID")
  )
  private Set<AStructure> listeStructures = new HashSet<>();
  /**
   * Relation avec type primitif.
   * Liste des UAI où le compte ENT est répliqué sur le réseau local.
   */
  @ElementCollection
  @CollectionTable(
    name = "externalId",
    joinColumns = @JoinColumn(name = "APersonne_id"),
    uniqueConstraints = {
      @UniqueConstraint(columnNames = {"id"}),
      @UniqueConstraint(name = "UK_externalid_userid_destinataire", columnNames = {"APersonne_id", "destinataire"})
    }
  )
  private Set<ExternalId> externalIds = new HashSet<>();

  /**
   * Constructeur de l'objet APersonne.java.
   */
  public APersonne() {
    super();
    this.forceEtat = ForceEtat.NONE;
  }

  /**
   * Constructeur de l'objet APersonne.java.
   *
   * @param anneeScolaire Année scolaire de validité de l'individu. Année à la rentrée de septembre.
   * @param categorie     Catégorie de la personne : Eleve, Enseignant ...
   * @param cleJointure   Clé de jointure, identifiant unique fourni par les différentes sources,
   *                      mais unique uniquement pour le périmètre de la source.
   * @param cn            Nom canonique de la personne : NOM + Prénom usuels.
   * @param givenName     Prénom usuel.
   * @param sn            Nom d'usage.
   */
  public APersonne(final Date anneeScolaire, final CategoriePersonne categorie, final CleJointure cleJointure,
                   final String cn, final String givenName, final String sn) {
    super();
    this.anneeScolaire = anneeScolaire;
    this.categorie = categorie;
    this.cleJointure = cleJointure;
    this.cn = cn;
    this.givenName = givenName;
    this.sn = sn;
    this.etat = Etat.Valide;
    this.forceEtat = ForceEtat.NONE;
  }

  /**
   * Transforme cette instance en chaine de caractères.
   *
   * @return <code>String</code> La chaine.
   * @see fr.recia.glc.db.entities.common.AbstractEntity#toString()
   */
  @Override
  public String toString() {
    return "APersonne [" +
      super.toString() + ", " +
      this.categorie + ", " +
      this.etat + ", " +
      this.forceEtat + ", " +
      this.cleJointure + ", " +
      this.uid + ", " +
      this.uuid + ", " +
      this.idEduConnect + ", " +
      this.login + ", " +
      this.alias + ", " +
      this.externalIds + ", " +
      this.cn + ", " +
      this.listeRouge + ", " +
      this.displayName + ", " +
      this.givenName + ", " +
      this.sn + ", " +
      this.patronyme + ", " +
      this.prenoms + ", " +
      this.password + ", " +
      this.adresse + ", " +
      this.civilite + ", " +
      this.sexe + ", " +
      this.titre + ", " +
      this.dateNaissance + ", " +
      this.email + ", " +
      this.numBureau + ", " +
      this.photo + ", " +
      this.fonctions + ", " +
      this.telephones + ", " +
      this.anneeScolaire + ", " +
      this.emailPersonnel + ", " +
      this.autresMails + ", " +
      this.doForward + ", " +
      this.validationCharte + ", " +
      this.groupes + ", " +
      this.incertains + ", " +
      this.structRattachement +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (cleJointure == null) {
      result = prime * result;
    } else {
      result = prime * result + cleJointure.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof APersonne)) {
      return false;
    }
    final APersonne other = (APersonne) obj;
    if (cleJointure == null) {
      if (other.cleJointure != null) {
        return false;
      }
    } else if (!cleJointure.equals(other.cleJointure)) {
      return false;
    }
    return true;
  }

}
