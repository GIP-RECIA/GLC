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
package fr.recia.glc.db.entities.application;

import fr.recia.glc.db.entities.common.AbstractEntity;
import fr.recia.glc.db.entities.groupe.Profil;
import fr.recia.glc.db.entities.groupe.RoleApplicatif;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.enums.CategorieApplication;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class Application extends AbstractEntity {

  /**
   * Catégorie de l'application.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I40)
  private CategorieApplication categorie;
  /**
   * Description de l'application.
   */
  private String description;
  /**
   * Identifiant unique de l'application.
   */
  @Column(unique = true, length = IntConst.I40)
  private String identifiant;
  /**
   * Nom unique de l'application, pouvant servir de login.
   */
  @Column(unique = true, length = IntConst.I100)
  private String nom;
  /**
   * Password de l'application.
   */
  private String password;

  /**
   * Relation bidirectionnelle.
   */
  @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  @JoinTable(
    name = "applications_profils",
    joinColumns = @JoinColumn(name = "APPLICATION_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "PROFIL_ID", referencedColumnName = "ID")
  )
  private Set<Profil> profils = new HashSet<>();
  /**
   * Relation unidirectionnelle.
   */
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.EAGER)
  @JoinColumn(name = "apersonne_fk")
  private APersonne proprietaire;
  /**
   * Relation bidirectionnelle.
   */
  @OneToMany(mappedBy = "proprietaire", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  private Set<RoleApplicatif> rolesApplicatifs = new HashSet<>();

  /**
   * Constructeur de l'objet Application.java.
   */
  public Application() {
    super();
  }

  /**
   * Constructeur de l'objet Application.java.
   *
   * @param identifiant Identifiant unique de l'application.
   * @param password    Password de l'application.
   * @param nom         Nom unique de l'application, pouvant servir de login.
   * @param categorie   Catégorie de l'application.
   */
  public Application(final String identifiant, final String password,
                     final String nom, final CategorieApplication categorie) {
    super();
    this.identifiant = identifiant;
    this.password = password;
    this.nom = nom;
    this.categorie = categorie;
  }

  @Override
  public String toString() {
    return "Application [" +
      super.toString() + ", " +
      this.categorie + ", " +
      this.identifiant + ", " +
      this.nom + ", " +
      this.description + ", " +
      this.password +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.categorie == null) {
      result = prime * result;
    } else {
      result = prime * result + this.categorie.hashCode();
    }
    if (this.identifiant == null) {
      result = prime * result;
    } else {
      result = prime * result + this.identifiant.hashCode();
    }
    if (this.nom == null) {
      result = prime * result;
    } else {
      result = prime * result + this.nom.hashCode();
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
    if (!(obj instanceof Application)) {
      return false;
    }
    final Application other = (Application) obj;
    if (this.categorie == null) {
      if (other.categorie != null) {
        return false;
      }
    } else if (!this.categorie.equals(other.categorie)) {
      return false;
    }
    if (this.identifiant == null) {
      if (other.identifiant != null) {
        return false;
      }
    } else if (!this.identifiant.equals(other.identifiant)) {
      return false;
    }
    if (this.nom == null) {
      if (other.nom != null) {
        return false;
      }
    } else if (!this.nom.equals(other.nom)) {
      return false;
    }
    return true;
  }

}
