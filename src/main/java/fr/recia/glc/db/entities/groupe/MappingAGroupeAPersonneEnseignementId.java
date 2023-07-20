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

import fr.recia.glc.db.entities.education.Enseignement;
import fr.recia.glc.db.entities.personne.Enseignant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class MappingAGroupeAPersonneEnseignementId implements Serializable {

  /**
   * The personne to associate to the groupe and enseignement.
   */
  @ManyToOne
  @JoinColumn(name = "APERSONNE_ID", nullable = false)
  private Enseignant enseignant;
  /**
   * The groupe to associate with the person and enseignement.
   */
  @ManyToOne
  @JoinColumn(name = "AGROUPEOFFONCCLASSEGROUPE_ID", nullable = false)
  private AGroupeOfFoncClasseGroupe groupe;
  /**
   * The enseignement to associate with the person and groupe.
   */
  @ManyToOne
  @JoinColumn(name = "ENSEIGNEMENT_ID", nullable = false)
  private Enseignement enseignement;

  /**
   * Contructor of the object MappingAGroupeAPersonneEnseignementId.java.
   */
  public MappingAGroupeAPersonneEnseignementId() {
    super();
  }

  /**
   * Contructor of the object MappingAGroupeAPersonneEnseignementId.java.
   *
   * @param enseignant
   * @param groupe       Une classe ou un groupe.
   * @param enseignement
   */
  public MappingAGroupeAPersonneEnseignementId(final Enseignant enseignant,
                                               final AGroupeOfFoncClasseGroupe groupe,
                                               final Enseignement enseignement) {
    super();
    this.enseignant = enseignant;
    this.groupe = groupe;
    this.enseignement = enseignement;
  }

  @Override
  public String toString() {
    return "MappingAGroupeAPersonneEnseignementId [enseignant=" +
      this.enseignant + ", groupe=" +
      this.groupe + ", enseignement=" +
      this.enseignement +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
      + ((enseignement == null) ? 0 : enseignement.hashCode());
    result = prime * result + ((groupe == null) ? 0 : groupe.hashCode());
    result = prime * result
      + ((enseignant == null) ? 0 : enseignant.hashCode());
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
    MappingAGroupeAPersonneEnseignementId other = (MappingAGroupeAPersonneEnseignementId) obj;
    if (enseignement == null) {
      if (other.enseignement != null)
        return false;
    } else if (!enseignement.equals(other.enseignement))
      return false;
    if (groupe == null) {
      if (other.groupe != null)
        return false;
    } else if (!groupe.equals(other.groupe))
      return false;
    if (enseignant == null) {
      if (other.enseignant != null)
        return false;
    } else if (!enseignant.equals(other.enseignant))
      return false;
    return true;
  }

}
