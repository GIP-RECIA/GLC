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

import fr.recia.glc.db.entities.education.MEF;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.enums.CategorieGroupe;
import lombok.Getter;
import lombok.Setter;

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
public class Classe extends AGroupeOfFoncClasseGroupe {

  /**
   * Mefs associé à la classe.
   */
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "classes_mefs",
    joinColumns = @JoinColumn(name = "CLASSE_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "MEF_ID", referencedColumnName = "ID")
  )
  private Set<MEF> mefs = new HashSet<>();

  /**
   * Constructeur de l'objet Classe.java.
   */
  public Classe() {
    super();
    this.setCategorie(CategorieGroupe.Classe);
  }

  /**
   * Constructeur de l'objet Classe.java.
   *
   * @param cn           Nom unique de la classe, peut servir comme identifiant.
   * @param membres      Liste des personnes membre de la classe.
   * @param proprietaire Etablissement ayant défini cette classe.
   * @param source       Source ayant créé l'objet.
   */
  public Classe(final String cn, final Set<MappingAGroupeAPersonne> membres,
                final Etablissement proprietaire, final String source) {
    super(cn, CategorieGroupe.Classe, membres, proprietaire, source);
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder("Classe [");
    sb.append(super.toString());
    if (this.mefs != null && !this.mefs.isEmpty()) {
      sb.append(", MEFs : [");
      for (MEF mef : this.mefs) {
        sb.append(mef.getCode());
        sb.append(", ");
      }
      sb.delete(sb.length() - 2, sb.length());
      sb.append("]");
    }
    sb.append("]");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return super.equals(obj);
  }

}
