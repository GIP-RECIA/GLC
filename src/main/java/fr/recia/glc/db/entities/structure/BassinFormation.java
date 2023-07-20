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
package fr.recia.glc.db.entities.structure;

import fr.recia.glc.db.entities.common.AbstractSimpleEntity;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
  @UniqueConstraint(columnNames = {"code", "source"})
})
@Getter
@Setter
public class BassinFormation extends AbstractSimpleEntity {

  /**
   * Code du bassin de formation.
   */
  @Column(nullable = false, length = IntConst.I10)
  private String code;
  /**
   * Nom du bassin de formation.
   */
  private String nom;
  /**
   * Source d'alimentation du bassin de formation.
   */
  @Column(nullable = false, length = IntConst.ISOURCE)
  private String source;

  /**
   * Constructeur de l'objet BassinFormation.java.
   */
  public BassinFormation() {
    super();
  }

  /**
   * Constructeur de l'objet BassinFormation.java.
   *
   * @param code   Code du bassin de formation.
   * @param nom    Nom du bassin de formation.
   * @param source Source d'alimentation de l'objet.
   */
  public BassinFormation(final String code, final String nom, final String source) {
    super();
    this.code = code;
    this.nom = nom;
    this.source = source;
  }

  @Override
  public String toString() {
    return "BassinFormation [" +
      super.toString() + ", " +
      this.code + ", " +
      this.nom + ", " +
      this.source +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.code == null) {
      result = prime * result;
    } else {
      result = prime * result + this.code.hashCode();
    }
    if (this.nom == null) {
      result = prime * result;
    } else {
      result = prime * result + this.nom.hashCode();
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
    if (!(obj instanceof BassinFormation)) {
      return false;
    }
    final BassinFormation other = (BassinFormation) obj;
    if (this.code == null) {
      if (other.code != null) {
        return false;
      }
    } else if (!this.code.equals(other.code)) {
      return false;
    }
    if (this.nom == null) {
      if (other.nom != null) {
        return false;
      }
    } else if (!this.nom.equals(other.nom)) {
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
