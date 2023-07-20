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
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class MinistereTutelle extends AbstractSimpleEntity {

  /**
   * Nom du ministère.
   */
  private String ministere;

  /**
   * Constructeur de l'objet MinistereTutelle.java.
   */
  public MinistereTutelle() {
    super();
  }

  /**
   * Constructeur de l'objet MinistereTutelle.java.
   *
   * @param ministere Ministère de tutelle.
   */
  public MinistereTutelle(final String ministere) {
    super();
    this.ministere = ministere;
  }

  @Override
  public String toString() {
    return "MinistereTutelle [" +
      super.toString() + ", " +
      this.ministere +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.ministere == null) {
      result = prime * result;
    } else {
      result = prime * result + this.ministere.hashCode();
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
    if (!(obj instanceof MinistereTutelle)) {
      return false;
    }
    final MinistereTutelle other = (MinistereTutelle) obj;
    if (this.ministere == null) {
      if (other.ministere != null) {
        return false;
      }
    } else if (!this.ministere.equals(other.ministere)) {
      return false;
    }
    return true;
  }

}
