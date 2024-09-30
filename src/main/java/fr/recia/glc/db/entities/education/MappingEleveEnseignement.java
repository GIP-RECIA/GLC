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
package fr.recia.glc.db.entities.education;

import fr.recia.glc.db.entities.personne.Eleve;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

@Embeddable
@Getter
@Setter
public class MappingEleveEnseignement implements Serializable {

  /**
   * Relation avec l'objet parent.
   */
  @Parent
  @Column(name = "ELEVE_ID", nullable = false)
  private Eleve eleve;
  /**
   * Source d'alimentation de l'association.
   */
  @Basic
  @Column(length = IntConst.ISOURCE, name = "SOURCE", nullable = false)
  private String source;
  /**
   * Relation avec l'enseignement.
   */
  @OneToOne
  @JoinColumn(name = "ENSEIGNEMENT_ID", nullable = false)
  private Enseignement enseignement;
  /**
   * Relation avec l'établissement.
   */
  @OneToOne
  @JoinColumn(name = "ETABLISSEMENT_ID")
  private Etablissement etablissement;

  /**
   * Contructor of the object MappingEleveEnseignement.java.
   */
  public MappingEleveEnseignement() {
    super();
  }

  /**
   * Contructor of the object MappingEleveEnseignement.java.
   *
   * @param source
   * @param enseignement
   */
  public MappingEleveEnseignement(final String source, final Enseignement enseignement) {
    super();
    this.source = source;
    this.enseignement = enseignement;
  }

  /**
   * Contructor of the object MappingEleveEnseignement.java.
   *
   * @param source
   * @param enseignement
   * @param etablissement
   */
  public MappingEleveEnseignement(final String source, final Enseignement enseignement, final Etablissement etablissement) {
    super();
    this.source = source;
    this.enseignement = enseignement;
    this.etablissement = etablissement;
  }

  @Override
  public String toString() {
    return "MappingEleveEnseignement [source=" +
      this.source + ", enseignement=" +
      this.enseignement + ", etablissement=" +
      (etablissement != null ? etablissement.getId() : "default") +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((eleve == null) ? 0 : eleve.hashCode());
    result = prime * result + ((enseignement == null) ? 0 : enseignement.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MappingEleveEnseignement other = (MappingEleveEnseignement) obj;
    if (enseignement == null) {
      if (other.enseignement != null)
        return false;
    } else if (!enseignement.equals(other.enseignement))
      return false;
    if (source == null) {
      return other.source == null;
    } else return source.equals(other.source);
  }

}
