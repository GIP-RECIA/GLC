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
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

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
@Table(name = "apersonnes_agroupes")
@AssociationOverrides({
  @AssociationOverride(
    name = "pk.personne",
    joinColumns = @JoinColumn(name = "APERSONNE_ID")),
  @AssociationOverride(
    name = "pk.groupe",
    joinColumns = @JoinColumn(name = "AGROUPEOFAPERS_ID"))
})
@Getter
@Setter
public class MappingAGroupeAPersonne implements Serializable {

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
  private MappingAGroupeAPersonneId pk = new MappingAGroupeAPersonneId();

  /**
   * Contructor of the object MappingAGroupeAPersonne.java.
   */
  public MappingAGroupeAPersonne() {
    super();
  }

  /**
   * Contructor of the object MappingAGroupeAPersonne.java.
   *
   * @param source
   * @param groupe
   * @param personne
   */
  public MappingAGroupeAPersonne(final String source, final APersonne personne, final AGroupeOfAPersonne groupe) {
    super();
    this.source = source;
    this.pk = new MappingAGroupeAPersonneId(personne, groupe);
  }

  @Override
  public String toString() {
    return "MappingAGroupeAPersonne [source=" +
      this.source + ", groupe=" +
      this.pk.getGroupe().getId() + ", personne=" +
      this.pk.getPersonne().getId() +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((pk == null) ? 0 : pk.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
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
    MappingAGroupeAPersonne other = (MappingAGroupeAPersonne) obj;
    if (pk == null) {
      if (other.pk != null)
        return false;
    } else if (!pk.equals(other.pk))
      return false;
    if (source == null) {
      return other.source == null;
    } else return source.equals(other.source);
  }

}
