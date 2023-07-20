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
package fr.recia.glc.db.entities.fonction;

import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.CategorieFonction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class FonctionDomaine extends AFonction {

  /**
   * Relation unidirectionnelle.
   * Domaines d'exercice.
   */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JoinTable(
    name = "fonctions_domaines",
    joinColumns = @JoinColumn(name = "FONCTIONDOMAINE_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "TYPEDOMAINE_ID", referencedColumnName = "ID")
  )
  private Set<TypeDomaine> domaines = new HashSet<>();
  /**
   * Relation unidirectionnelle.
   * Structure d'exercice des domaines.
   */
  @ManyToOne
  @JoinColumn(name = "astructure_fk", insertable = false, updatable = false)
  private AStructure structure;

  /**
   * Constructeur de l'objet FonctionDomaine.java.
   */
  public FonctionDomaine() {
    super();
    this.setCategorie(CategorieFonction.Domaine);
  }

  /**
   * Constructeur de l'objet FonctionDomaine.java.
   *
   * @param domaines  Domaines d'exercice.
   * @param structure Structure d'exercice des domaines.
   * @param personne  Personne exerçant ce domaine.
   * @param source    Source d'alimentation gérant cette fonction.
   */
  public FonctionDomaine(final Set<TypeDomaine> domaines, final AStructure structure,
                         final APersonne personne, final String source) {
    super(CategorieFonction.Domaine, personne, source);
    this.domaines = domaines;
    this.structure = structure;
  }

  @Override
  public String toString() {
    return "FonctionDomaine [" +
      super.toString() + ", " +
      this.structure + ", " +
      this.domaines +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    if (this.domaines == null) {
      result = prime * result;
    } else {
      result = prime * result + this.domaines.hashCode();
    }
    if (this.structure == null) {
      result = prime * result;
    } else {
      result = prime * result + this.structure.hashCode();
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
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof FonctionDomaine)) {
      return false;
    }
    final FonctionDomaine other = (FonctionDomaine) obj;
    if (this.domaines == null) {
      if (other.domaines != null) {
        return false;
      }
    } else if (!this.domaines.equals(other.domaines)) {
      return false;
    }
    if (this.structure == null) {
      if (other.structure != null) {
        return false;
      }
    } else if (!this.structure.equals(other.structure)) {
      return false;
    }
    return true;
  }

}
