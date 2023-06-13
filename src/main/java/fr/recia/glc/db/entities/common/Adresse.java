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

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
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

}
