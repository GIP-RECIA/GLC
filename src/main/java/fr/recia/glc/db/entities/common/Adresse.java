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

import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class Adresse implements Serializable {

  /**
   * Champ libre d'une adresse.
   */
  private String adresse;
  /**
   * Code postal.
   */
  @Column(length = IntConst.I40)
  private String codePostal;
  /**
   * Nom de la ville.
   */
  private String ville;
  /**
   * Boite postale dans le cas d'une structure.
   */
  @Column(length = IntConst.I40)
  private String boitePostale;
  /**
   * Pays.
   */
  private String pays;

  /**
   * Constructeur de l'objet Adresse.java.
   */
  public Adresse() {
    super();
  }

  /**
   * Constructeur de l'objet Adresse.java.
   *
   * @param adresse    Champ libre d'une adresse.
   * @param codePostal Code postal.
   * @param ville      Nom de la ville.
   */
  public Adresse(final String adresse, final String codePostal, final String ville) {
    super();
    this.adresse = adresse;
    this.codePostal = codePostal;
    this.ville = ville;
  }

  @Override
  public String toString() {
    return "Adresse [" +
      this.adresse + ", " +
      this.codePostal + ", " +
      this.ville + ", " +
      this.boitePostale + ", " +
      this.pays +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.adresse == null) {
      result = prime * result;
    } else {
      result = prime * result + this.adresse.hashCode();
    }
    if (this.boitePostale == null) {
      result = prime * result;
    } else {
      result = prime * result + this.boitePostale.hashCode();
    }
    if (this.codePostal == null) {
      result = prime * result;
    } else {
      result = prime * result + this.codePostal.hashCode();
    }
    if (this.pays == null) {
      result = prime * result;
    } else {
      result = prime * result + this.pays.hashCode();
    }
    if (this.ville == null) {
      result = prime * result;
    } else {
      result = prime * result + this.ville.hashCode();
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
    if (!(obj instanceof Adresse)) {
      return false;
    }
    final Adresse other = (Adresse) obj;
    if (this.adresse == null) {
      if (other.adresse != null) {
        return false;
      }
    } else if (!this.adresse.equals(other.adresse)) {
      return false;
    }
    if (this.boitePostale == null) {
      if (other.boitePostale != null) {
        return false;
      }
    } else if (!this.boitePostale.equals(other.boitePostale)) {
      return false;
    }
    if (this.codePostal == null) {
      if (other.codePostal != null) {
        return false;
      }
    } else if (!this.codePostal.equals(other.codePostal)) {
      return false;
    }
    if (this.pays == null) {
      if (other.pays != null) {
        return false;
      }
    } else if (!this.pays.equals(other.pays)) {
      return false;
    }
    if (this.ville == null) {
      return other.ville == null;
    } else return this.ville.equals(other.ville);
  }

}
