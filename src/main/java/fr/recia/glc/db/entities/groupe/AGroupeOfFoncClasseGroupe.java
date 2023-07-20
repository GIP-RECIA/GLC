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

import fr.recia.glc.db.entities.fonction.FonctionClasseGroupe;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.enums.CategorieGroupe;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public abstract class AGroupeOfFoncClasseGroupe extends AGroupeOfAPersonne {

  /**
   * Relation bidirectionnelle.
   * Listes des personnes ayant une fonction au sein
   * de la classe ou du groupe avec leur fonction.
   */
  @OneToMany(mappedBy = "classeGroupe", fetch = FetchType.LAZY)
  private Set<FonctionClasseGroupe> fonctions = new HashSet<>();
  /**
   * Relation bidirectionnelle.
   * Associations personne-enseignement-groupe.
   */
  @OneToMany(mappedBy = "pk.groupe", fetch = FetchType.LAZY)
  private Set<MappingAGroupeAPersonneEnseignement> ProfsEnseignements = new HashSet<>();
  /**
   * Relation unidirectionnelle.
   * Etablissement ayant défini cette classe ou ce groupe.
   */
  @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH, CascadeType.PERSIST}, fetch = FetchType.EAGER)
  @JoinColumn(name = "etablissement_fk")
  private Etablissement proprietaire;

  /**
   * Constructeur de l'objet AGroupeOfFoncClasseGroupe.java.
   */
  public AGroupeOfFoncClasseGroupe() {
    super();
  }

  /**
   * Constructeur de l'objet AGroupeOfFoncClasseGroupe.java.
   *
   * @param cn           Nom unique de groupe, peut servir comme identifiant.
   * @param categorie    Type de groupe.
   * @param membres      Liste des personnes membres du groupe.
   * @param proprietaire Etablissement ayant défini cette classe ou ce groupe.
   * @param source       Source ayant créé l'objet.
   */
  public AGroupeOfFoncClasseGroupe(final String cn, final CategorieGroupe categorie,
                                   final Set<MappingAGroupeAPersonne> membres, final Etablissement proprietaire, final String source) {
    super(cn, categorie, membres, source);
    this.proprietaire = proprietaire;
  }

  @Override
  public String toString() {
    return "AGroupeOfFoncClasseGroupe [" +
      super.toString() + ", " +
      this.proprietaire +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    if (this.proprietaire == null) {
      result = prime * result;
    } else {
      result = prime * result + this.proprietaire.hashCode();
    }
    return result;
  }

  /**
   * Teste si un objet est égal à cette instance.
   *
   * @param obj l'instance le l'object à comparer.
   * @return <code>boolean</code> : vrai si l'instance est identique, faux sinon
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof AGroupeOfFoncClasseGroupe)) {
      return false;
    }
    final AGroupeOfFoncClasseGroupe other = (AGroupeOfFoncClasseGroupe) obj;
    if (this.proprietaire == null) {
      if (other.proprietaire != null) {
        return false;
      }
    } else if (!this.proprietaire.equals(other.proprietaire)) {
      return false;
    }
    return true;
  }

}
