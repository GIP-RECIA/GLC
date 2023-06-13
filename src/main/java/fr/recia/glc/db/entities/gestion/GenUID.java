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

import fr.recia.glc.db.entities.common.AbstractEntity;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = {
  @UniqueConstraint(columnNames = {"l", "c", "xx"})
})
@Getter
@Setter
public class GenUID extends AbstractEntity {

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

}
