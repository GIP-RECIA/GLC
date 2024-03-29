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
package fr.recia.glc.db.entities.gestion;

import fr.recia.glc.db.entities.common.AbstractTracedEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
public class AnneeScolaire extends AbstractTracedEntity {

  /**
   * Informe de l'année en cours, une seule insertion par année possible,
   * année à la rentrée (de septembre).
   */
  @Column(unique = true, nullable = false)
  @Temporal(TemporalType.DATE)
  private Date anneeEnCours;
  /**
   * Date exacte du passage à l'année suivante pour les insertions.
   */
  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date passageAnneeSuivante;
  /**
   * Date de fin des autorisations des accés pour les utilisateurs de l'année scolaire précédente.
   * Si null on y place la date de passageAnneeSuivante.
   */
  @Column(nullable = false)
  @Temporal(TemporalType.DATE)
  private Date finAutorisation;
  /**
   * Booleen indiquant l'insertion du complet des établissements en début d'année scolaire.
   */
  @Column(nullable = false, columnDefinition = "BIT")
  private boolean etabMAJ;

  /**
   * Constructeur de l'objet AnneeScolaire.java.
   */
  public AnneeScolaire() {
    super();
  }

  /**
   * Constructeur de l'objet AnneeScolaire.java.
   *
   * @param anneeEnCours         Année scolaire en cours, année à la rentrée (de septembre).
   * @param passageAnneeSuivante Date de passage des insertions à l'année suivante.
   * @param finAutorisation      Date de fin des autorisations des accés
   *                             pour les utilisateurs de l'année scolaire précédente.
   */
  public AnneeScolaire(final Date anneeEnCours, final Date passageAnneeSuivante, final Date finAutorisation) {
    super();
    this.anneeEnCours = anneeEnCours;
    this.passageAnneeSuivante = passageAnneeSuivante;
    this.finAutorisation = finAutorisation;
  }

  @Override
  public String toString() {
    return "AnneeScolaire [" +
      super.toString() + ", " +
      this.anneeEnCours + ", " +
      this.passageAnneeSuivante + ", " +
      this.finAutorisation + ", " +
      this.etabMAJ +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.anneeEnCours == null) {
      result = prime * result;
    } else {
      result = prime * result + this.anneeEnCours.hashCode();
    }
    if (this.finAutorisation == null) {
      result = prime * result;
    } else {
      result = prime * result + this.finAutorisation.hashCode();
    }
    if (this.passageAnneeSuivante == null) {
      result = prime * result;
    } else {
      result = prime * result + this.passageAnneeSuivante.hashCode();
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
    if (!(obj instanceof AnneeScolaire)) {
      return false;
    }
    final AnneeScolaire other = (AnneeScolaire) obj;
    if (this.anneeEnCours == null) {
      if (other.anneeEnCours != null) {
        return false;
      }
    } else if (!this.anneeEnCours.equals(other.anneeEnCours)) {
      return false;
    }
    if (this.finAutorisation == null) {
      if (other.finAutorisation != null) {
        return false;
      }
    } else if (!this.finAutorisation.equals(other.finAutorisation)) {
      return false;
    }
    if (this.passageAnneeSuivante == null) {
      if (other.passageAnneeSuivante != null) {
        return false;
      }
    } else if (!this.passageAnneeSuivante.equals(other.passageAnneeSuivante)) {
      return false;
    }
    return true;
  }

}
