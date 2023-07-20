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
package fr.recia.glc.db.entities.groupe;

import fr.recia.glc.db.entities.common.AbstractEntity;
import fr.recia.glc.db.enums.CategorieGroupe;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Entity
@Getter
@Setter
public abstract class AGroupe extends AbstractEntity {

  /**
   * Type de groupe.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I30)
  private CategorieGroupe categorie;
  /**
   * Année scolaire de validité de l'objet.
   * Année à la rentrée de septembre.
   */
  @Temporal(TemporalType.DATE)
  private Date anneeScolaire;
  /**
   * Nom unique de groupe, peut servir comme identifiant au sein d'un établissement.
   */
  private String cn;
  /**
   * Description du groupe.
   */
  private String description;
  /**
   * Source ayant créé le groupe.
   */
  @Column(length = IntConst.ISOURCE)
  private String source;

  /**
   * Constructeur de l'objet AGroupe.java.
   */
  public AGroupe() {
    super();
  }

  /**
   * Constructeur de l'objet AGroupe.java.
   *
   * @param cn        Nom unique de groupe, peut servir comme identifiant.
   * @param categorie Type de groupe.
   * @param source    Source ayant créé l'objet.
   */
  public AGroupe(final String cn, final CategorieGroupe categorie, final String source) {
    super();
    this.cn = cn;
    this.categorie = categorie;
    this.source = source;
  }

  @Override
  public String toString() {
    return "AGroupe [" +
      super.toString() + ", " +
      this.categorie + ", " +
      this.cn + ", " +
      this.source + ", " +
      this.anneeScolaire + ", " +
      this.description +
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
    if (this.cn == null) {
      result = prime * result;
    } else {
      result = prime * result + this.cn.hashCode();
    }
    if (this.source == null) {
      result = prime * result;
    } else {
      result = prime * result + this.source.hashCode();
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
    if (!(obj instanceof AGroupe)) {
      return false;
    }
    final AGroupe other = (AGroupe) obj;
    if (this.categorie == null) {
      if (other.categorie != null) {
        return false;
      }
    } else if (!this.categorie.equals(other.categorie)) {
      return false;
    }
    if (this.cn == null) {
      if (other.cn != null) {
        return false;
      }
    } else if (!this.cn.equalsIgnoreCase(other.cn)) {
      return false;
    }
    if (this.source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!this.source.equals(other.source)) {
      return false;
    }
    return true;
  }

}
