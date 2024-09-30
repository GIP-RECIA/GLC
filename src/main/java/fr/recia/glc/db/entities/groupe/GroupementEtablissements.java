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

import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.enums.CategorieGroupe;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class GroupementEtablissements extends AGroupe {

  /**
   * Relation bidirectionnelle.
   * Liste des Etablissements membres du groupe.
   */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JoinTable(name = "groupements_etablissements",
    joinColumns = @JoinColumn(name = "GROUPEMENTETABLISSEMENT_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "ETABLISSEMENT_ID", referencedColumnName = "ID")
  )
  private Set<Etablissement> membres = new HashSet<>();

  /**
   * Constructeur de l'objet GroupementEtablissements.java.
   */
  public GroupementEtablissements() {
    super();
    this.setCategorie(CategorieGroupe.Groupement_etablissement);
  }

  /**
   * Constructeur de l'objet GroupementEtablissements.java.
   *
   * @param cn      Nom unique de groupe, peut servir comme identifiant.
   * @param membres Liste des Etablissements membres du groupe.
   * @param source  Source ayant créé l'objet.
   */
  public GroupementEtablissements(final String cn, final Set<Etablissement> membres, final String source) {
    super(cn, CategorieGroupe.Groupement_etablissement, source);
    this.membres = membres;
  }

  @Override
  public String toString() {
    return "GroupementEtablissement [" + super.toString() + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    if (this.membres == null) {
      result = prime * result;
    } else {
      result = prime * result + this.membres.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof GroupementEtablissements)) {
      return false;
    }
    final GroupementEtablissements other = (GroupementEtablissements) obj;
    if (this.membres == null) {
      return other.membres == null;
    } else return this.membres.equals(other.membres);
  }

}
