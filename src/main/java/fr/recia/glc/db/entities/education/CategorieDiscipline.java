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
package fr.recia.glc.db.entities.education;

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
public class CategorieDiscipline extends AbstractSimpleEntity {

  /**
   * Code de la catégorie de discipline.
   */
  @Column(nullable = false, length = IntConst.I60)
  private String code;
  /**
   * Libellé de la catégorie de discipline de poste.
   */
  private String pivotDiscipline;
  /**
   * Source d'alimentation de la catégorie de discipline de poste.
   */
  @Column(nullable = false, length = IntConst.ISOURCE)
  private String source;

  /**
   * Constructeur de l'objet CategorieDiscipline.java.
   */
  public CategorieDiscipline() {
    super();
  }

  /**
   * Constructeur de l'objet CategorieDiscipline.java.
   *
   * @param code            Code de la catégorie de discipline.
   * @param pivotDiscipline Libellé de la catégorie de discipline de poste.
   * @param source          La source d'alimentation de l'objet.
   */
  public CategorieDiscipline(final String code, final String pivotDiscipline, final String source) {
    super();
    this.code = code;
    this.pivotDiscipline = pivotDiscipline;
    this.source = source;
  }

  @Override
  public String toString() {
    return "CategorieDiscipline [" +
      super.toString() + ", " +
      this.code + ", " +
      this.pivotDiscipline + ", " +
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
    if (this.pivotDiscipline == null) {
      result = prime * result;
    } else {
      result = prime * result + this.pivotDiscipline.hashCode();
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
    if (!(obj instanceof CategorieDiscipline)) {
      return false;
    }
    final CategorieDiscipline other = (CategorieDiscipline) obj;
    if (this.code == null) {
      if (other.code != null) {
        return false;
      }
    } else if (!this.code.equals(other.code)) {
      return false;
    }
    if (this.pivotDiscipline == null) {
      if (other.pivotDiscipline != null) {
        return false;
      }
    } else if (!this.pivotDiscipline.equals(other.pivotDiscipline)) {
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
