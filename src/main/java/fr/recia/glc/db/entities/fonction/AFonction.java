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
package fr.recia.glc.db.entities.fonction;

import fr.recia.glc.db.entities.common.AbstractEntity;
import fr.recia.glc.db.enums.CategorieFonction;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class AFonction extends AbstractEntity {

  /**
   * Categorie de la fonction.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I20)
  private CategorieFonction categorie;
  /**
   * Source d'alimentation de la fonction.
   */
  @Column(length = IntConst.ISOURCE)
  private String source;

  /**
   * Relation bidirectionnelle.
   *
   * @see fr.recia.glc.db.entities.personne.APersonne
   */
  @ManyToOne
  @JoinColumn(name = "personne_fk")
  private APersonne personne;

  /**
   * Constructeur de l'objet AFonction.java.
   */
  public AFonction() {
    super();
  }

  /**
   * Constructeur de l'objet AFonction.java.
   *
   * @param categorie Categorie de la fonction.
   * @param personne  Personne ayant cette fonction.
   * @param source    Source d'alimentation gérant cette fonction.
   */
  public AFonction(final CategorieFonction categorie, final APersonne personne, final String source) {
    super();
    this.categorie = categorie;
    this.source = source;
    this.personne = personne;
  }

  @Override
  public String toString() {
    return "AFonction [" +
      super.toString() + ", " +
      this.categorie + ", " +
      this.source +
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
    if (this.source == null) {
      result = prime * result;
    } else {
      result = prime * result + this.source.hashCode();
    }
    if (this.personne == null) {
      result = prime * result;
    } else {
      result = prime * result + this.personne.hashCode();
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
    if (!(obj instanceof AFonction)) {
      return false;
    }
    final AFonction other = (AFonction) obj;
    if (categorie == null) {
      if (other.categorie != null) {
        return false;
      }
    } else if (!categorie.equals(other.categorie)) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    if (personne == null) {
      if (other.personne != null) {
        return false;
      }
    } else if (!personne.equals(other.personne)) {
      return false;
    }
    return true;
  }

}
