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
import fr.recia.glc.db.utils.IntConst;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "apersonnes_agroupes_enseignements")
@AssociationOverrides({
  @AssociationOverride(name = "pk.enseignant", joinColumns = @JoinColumn(name = "APERSONNE_ID")),
  @AssociationOverride(name = "pk.groupe", joinColumns = @JoinColumn(name = "AGROUPEOFFONCCLASSEGROUPE_ID")),
  @AssociationOverride(name = "pk.enseignement", joinColumns = @JoinColumn(name = "ENSEIGNEMENT_ID"))
})
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class MappingAGroupeAPersonneEnseignement implements Serializable {

  /**
   * The Source which insert the entry.
   */
  @Basic
  @Column(name = "SOURCE", length = IntConst.ISOURCE, nullable = false)
  private String source;

  /**
   * The pk
   */
  @EmbeddedId
  private MappingAGroupeAPersonneEnseignementId pk = new MappingAGroupeAPersonneEnseignementId();

  /**
   * Contructor of the object MappingAGroupeAPersonneEnseignement.java.
   *
   * @param source
   * @param groupe
   * @param enseignant
   * @param enseignement
   */
  public MappingAGroupeAPersonneEnseignement(final String source, final Enseignant enseignant,
                                             final AGroupeOfFoncClasseGroupe groupe, final Enseignement enseignement) {
    this.source = source;
    this.pk = new MappingAGroupeAPersonneEnseignementId(enseignant, groupe, enseignement);
  }

}
