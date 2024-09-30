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
package fr.recia.glc.db.entities.relation;

import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.enums.CategorieRelation;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.AssociationOverride;
import javax.persistence.AssociationOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "relations_apersonnes")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@AssociationOverrides({
  @AssociationOverride(name = "pk.personne1", joinColumns = @JoinColumn(name = "APERSONNE1_ID")),
  @AssociationOverride(name = "pk.personne2", joinColumns = @JoinColumn(name = "APERSONNE2_ID"))
})
@Getter
@Setter
public abstract class AMappingRelation implements Serializable {

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
  private MappingAPersonneAPersonneId pk = new MappingAPersonneAPersonneId();

  /**
   * Empty Constructor, must not be used.
   */
  public AMappingRelation() {
    super();
  }

  /**
   * Contructor of the object MappingAGroupeAPersonne.java.
   *
   * @param source
   * @param personne1
   * @param personne2
   */
  public AMappingRelation(final String source, final APersonne personne1,
                          final APersonne personne2, final CategorieRelation categoryRelation) {
    super();
    this.source = source;
    this.pk = new MappingAPersonneAPersonneId(personne1, personne2, categoryRelation);
  }

  @Override
  public String toString() {
    return "MappingAPersonneAPersonne [source=" +
      source + ", categoryRelation=" +
      this.pk.getCategorie() + ", personne1=" +
      this.pk.getPersonne1().getId() + ", personne2=" +
      this.pk.getPersonne2().getId() +
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
    AMappingRelation other = (AMappingRelation) obj;
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
