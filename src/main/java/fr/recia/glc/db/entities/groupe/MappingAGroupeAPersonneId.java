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
package fr.recia.glc.db.entities.groupe;

import fr.recia.glc.db.entities.personne.APersonne;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class MappingAGroupeAPersonneId implements Serializable {

  /**
   * The personne to associate to the groupe.
   */
  @ManyToOne(cascade = CascadeType.REFRESH)
  @JoinColumn(name = "APERSONNE_ID", nullable = false)
  private APersonne personne;

  /**
   * The groupe to associate with the person.
   */
  @ManyToOne
  @JoinColumn(name = "AGROUPEOFAPERS_ID", nullable = false)
  private AGroupeOfAPersonne groupe;

  /**
   * Contructor of the object MappingAGroupeAPersonneId.java.
   */
  public MappingAGroupeAPersonneId() {
    super();
  }

  /**
   * Contructor of the object MappingAGroupeAPersonne.java.
   *
   * @param groupe
   * @param personne
   */
  public MappingAGroupeAPersonneId(final APersonne personne, final AGroupeOfAPersonne groupe) {
    super();
    this.groupe = groupe;
    this.personne = personne;
  }

  @Override
  public String toString() {
    return "MappingAGroupeAPersonneId [personne=" +
      this.personne.getId() + ", groupe=" +
      this.groupe.getId() +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((groupe == null) ? 0 : groupe.hashCode());
    result = prime * result + ((personne == null) ? 0 : personne.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MappingAGroupeAPersonneId other = (MappingAGroupeAPersonneId) obj;
    if (groupe == null) {
      if (other.groupe != null)
        return false;
    } else if (!groupe.equals(other.groupe))
      return false;
    if (personne == null) {
      if (other.personne != null)
        return false;
    } else if (!personne.equals(other.personne))
      return false;
    return true;
  }

}
