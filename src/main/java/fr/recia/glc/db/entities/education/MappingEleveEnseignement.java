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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
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
@NoArgsConstructor
@EqualsAndHashCode
@ToString
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
   *
   * @param source
   * @param enseignement
   */
  public MappingEleveEnseignement(final String source, final Enseignement enseignement) {
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
    this.source = source;
    this.enseignement = enseignement;
    this.etablissement = etablissement;
  }

}
