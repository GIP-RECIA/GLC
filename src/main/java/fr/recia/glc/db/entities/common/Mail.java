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

import fr.recia.glc.db.entities.common.enums.MailType;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Getter
@Setter
public class Mail extends AbstractSimpleEntity {

  /**
   * Adresse mail.
   */
  @Column(nullable = false)
  private String adresse;
  /**
   * Type de l'adresse.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I20)
  private MailType type;
  /**
   * Source de l'alimentation.
   */
  @Column(nullable = false, length = IntConst.ISOURCE)
  private String source;
  /**
   * Si adresse validée
   */
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
  private boolean validated;

  /**
   * Constructeur de l'objet Mail.java.
   */
  public Mail() {
    super();
  }

  /**
   * Constructeur de l'objet Mail.java.
   *
   * @param adresse   Adresse mail.
   * @param type      Le type d'usage.
   * @param source    La source dd'alimentation.
   * @param validated Si l'adresse a été vérifiée ou si de source sûre.
   */
  public Mail(String adresse, MailType type, String source, boolean validated) {
    super();
    this.adresse = adresse;
    this.type = type;
    this.source = source;
    this.validated = validated;
  }

  /**
   * Transforme cette instance en chaine de caractères.
   *
   * @return <code>String</code> La chaine.
   * @see fr.recia.glc.db.entities.common.AbstractEntity#toString()
   */
  @Override
  public String toString() {
    return "Mail [" +
      super.toString() + ", " +
      this.adresse + ", " +
      this.type + ", " +
      this.validated + ", " +
      this.source +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (adresse == null) {
      result = prime * result;
    } else {
      result = prime * result + adresse.hashCode();
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
    if (!(obj instanceof Mail)) {
      return false;
    }
    final Mail other = (Mail) obj;
    if (adresse == null) {
      if (other.adresse != null) {
        return false;
      }
    } else if (!adresse.equals(other.adresse)) {
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
