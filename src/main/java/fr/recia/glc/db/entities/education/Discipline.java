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

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
  @UniqueConstraint(columnNames = {"code", "source"})
})
@Getter
@Setter
public class Discipline extends AbstractSimpleEntity {

  /**
   * Code de la discipline.
   */
  @Column(nullable = false, length = IntConst.I30)
  private String code;
  /**
   * Libellé de la discipline de poste.
   */
  private String disciplinePoste;
  /**
   * Source d'alimentation de la discipline de poste.
   */
  @Column(nullable = false, length = IntConst.ISOURCE)
  private String source;

  /**
   * Constructeur de l'objet Discipline.java.
   */
  public Discipline() {
    super();
  }

  /**
   * Constructeur de l'objet Discipline.java.
   *
   * @param code            Code de la discipline.
   * @param disciplinePoste Libellé de la discipline de poste.
   * @param source          Source d'alimentation de l'objet.
   */
  public Discipline(final String code, final String disciplinePoste, final String source) {
    super();
    this.code = code;
    this.disciplinePoste = disciplinePoste;
    this.source = source;
  }

  @Override
  public String toString() {
    return "Discipline [" +
      super.toString() + ", " +
      this.code + ", " +
      this.disciplinePoste + ", " +
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
    if (this.disciplinePoste == null) {
      result = prime * result;
    } else {
      result = prime * result + this.disciplinePoste.hashCode();
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
    if (!(obj instanceof Discipline)) {
      return false;
    }
    final Discipline other = (Discipline) obj;
    if (this.code == null) {
      if (other.code != null) {
        return false;
      }
    } else if (!this.code.equals(other.code)) {
      return false;
    }
    if (this.disciplinePoste == null) {
      if (other.disciplinePoste != null) {
        return false;
      }
    } else if (!this.disciplinePoste.equals(other.disciplinePoste)) {
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
