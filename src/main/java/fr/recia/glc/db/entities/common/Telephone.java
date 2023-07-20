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
package fr.recia.glc.db.entities.common;

import fr.recia.glc.db.enums.TelephoneType;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
public class Telephone extends AbstractSimpleEntity {

  /**
   * Numéro de téléphone.
   */
  @Column(nullable = false, length = IntConst.I20)
  private String numero;
  /**
   * Type du numéro de téléphone.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I20)
  private TelephoneType type;
  /**
   * Source de l'alimentation.
   */
  @Column(nullable = false, length = IntConst.ISOURCE)
  private String source;
  /**
   * Si téléphone utilisé.
   */
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
  private boolean used;

  /**
   * Constructeur de l'objet Telephone.java.
   */
  public Telephone() {
    super();
  }

  /**
   * Constructeur de l'objet Telephone.java avec les champs obligatoires.
   *
   * @param numero Numéro de téléphone.
   * @param type   Type du numéro de téléphone (FAX, mobile, fixe pro, fixe pers).
   * @param source source d'alimentation
   * @param used   Si le numéro est utilisé ou non.
   */
  public Telephone(final String numero, final TelephoneType type, final String source, final boolean used) {
    super();
    this.numero = numero;
    this.type = type;
    this.source = source;
    this.used = used;
  }

  @Override
  public String toString() {
    return "Telephone [" +
      super.toString() + ", " +
      this.type + ", " +
      this.numero + ", " +
      this.source + ", " +
      this.used +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (numero == null) {
      result = prime * result;
    } else {
      result = prime * result + numero.hashCode();
    }
    if (type == null) {
      result = prime * result;
    } else {
      result = prime * result + type.hashCode();
    }
    if (source == null) {
      result = prime * result;
    } else {
      result = prime * result + source.hashCode();
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
    if (!(obj instanceof Telephone)) {
      return false;
    }
    final Telephone other = (Telephone) obj;
    if (numero == null) {
      if (other.numero != null) {
        return false;
      }
    } else if (!numero.equals(other.numero)) {
      return false;
    }
    if (type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!type.equals(other.type)) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    return true;
  }

}
