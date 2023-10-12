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
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
  @UniqueConstraint(columnNames = {"l", "c", "xx"})
})
@Getter
@Setter
public class GenUID extends AbstractTracedEntity {

  /**
   * Code région.
   */
  @Column(length = IntConst.I1)
  private String l;
  /**
   * Code département.
   */
  @Column(length = IntConst.I1)
  private String c;
  /**
   * Code de l'année d'insertion.
   */
  @Column(length = IntConst.I2)
  private String xx;
  /**
   * Code alphanumérique autoincrémenté, sauvegardé sous forme d'entier.
   */
  private int iiii;

  /**
   * Constructeur de l'objet GenUID.java.
   */
  public GenUID() {
    super();
  }

  /**
   * Constructeur de l'objet GenUID.java.
   *
   * @param l Code région.
   * @param c Code département.
   */
  public GenUID(final String l, final String c) {
    super();
    this.l = l;
    this.c = c;
  }

  @Override
  public String toString() {
    return "GenUID [" +
      super.toString() + ", " +
      this.l + ", " +
      this.xx + ", " +
      this.c + ", " +
      this.iiii +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.c == null) {
      result = prime * result;
    } else {
      result = prime * result + this.c.hashCode();
    }
    result = prime * result + this.iiii;
    if (this.l == null) {
      result = prime * result;
    } else {
      result = prime * result + this.l.hashCode();
    }
    if (this.xx == null) {
      result = prime * result;
    } else {
      result = prime * result + this.xx.hashCode();
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
    if (!(obj instanceof GenUID)) {
      return false;
    }
    final GenUID other = (GenUID) obj;
    if (this.c == null) {
      if (other.c != null) {
        return false;
      }
    } else if (!this.c.equals(other.c)) {
      return false;
    }
    if (this.iiii != other.iiii) {
      return false;
    }
    if (this.l == null) {
      if (other.l != null) {
        return false;
      }
    } else if (!this.l.equals(other.l)) {
      return false;
    }
    if (this.xx == null) {
      if (other.xx != null) {
        return false;
      }
    } else if (!this.xx.equals(other.xx)) {
      return false;
    }
    return true;
  }

}
