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
package fr.recia.glc.db.entities.fonction;

import fr.recia.glc.db.entities.common.AbstractSimpleEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class TypeDomaine extends AbstractSimpleEntity {

  /**
   * Nom du domaine d'exercice.
   */
  private String libelleDomaine;

  /**
   * Constructeur de l'objet TypeDomaine.java.
   */
  public TypeDomaine() {
    super();
  }

  /**
   * Constructeur de l'objet TypeDomaine.java.
   *
   * @param libelle Nom du domaine d'exercice.
   */
  public TypeDomaine(final String libelle) {
    super();
    this.libelleDomaine = libelle;
  }

  @Override
  public String toString() {
    return "TypeDomaine [" +
      super.toString() + ", " +
      this.libelleDomaine +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.libelleDomaine == null) {
      result = prime * result;
    } else {
      result = prime * result + this.libelleDomaine.hashCode();
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
    if (!(obj instanceof TypeDomaine)) {
      return false;
    }
    final TypeDomaine other = (TypeDomaine) obj;
    if (this.libelleDomaine == null) {
      if (other.libelleDomaine != null) {
        return false;
      }
    } else if (!this.libelleDomaine.equals(other.libelleDomaine)) {
      return false;
    }
    return true;
  }

}
