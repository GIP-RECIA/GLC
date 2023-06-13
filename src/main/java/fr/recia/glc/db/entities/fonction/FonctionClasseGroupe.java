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

import fr.recia.glc.db.entities.common.enums.CategorieFonction;
import fr.recia.glc.db.entities.common.enums.TypeClasse;
import fr.recia.glc.db.entities.groupe.AGroupeOfFoncClasseGroupe;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class FonctionClasseGroupe extends AFonction {

  /**
   * Type énuméré du type de fonction.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I30)
  private TypeClasse type;

  /**
   * Relation bidirectionnelle.
   * Classe ou groupe concerné.
   */
  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinColumn(name = "classe_groupe_fk")
  private AGroupeOfFoncClasseGroupe classeGroupe;

  /**
   * Constructeur de l'objet FonctionClasseGroupe.java.
   */
  public FonctionClasseGroupe() {
    super();
    this.setCategorie(CategorieFonction.Classe);
  }

  /**
   * Constructeur de l'objet FonctionClasseGroupe.java.
   *
   * @param type         Type énuméré du type de fonction.
   * @param personne     Personne ayant cette fonction.
   * @param classeGroupe Classe ou groupe concerné.
   * @param source       Source d'alimentation gérant cette fonction.
   */
  public FonctionClasseGroupe(final TypeClasse type, final APersonne personne,
                              final AGroupeOfFoncClasseGroupe classeGroupe, final String source) {
    super(CategorieFonction.Classe, personne, source);
    this.type = type;
    this.classeGroupe = classeGroupe;
  }

  @Override
  public String toString() {
    return "FonctionClasseGroupe [" +
      super.toString() + ", " +
      this.type + ", " +
      this.classeGroupe +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    if (this.classeGroupe == null) {
      result = prime * result;
    } else {
      result = prime * result + this.classeGroupe.hashCode();
    }
    if (this.type == null) {
      result = prime * result;
    } else {
      result = prime * result + this.type.hashCode();
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
    if (!(obj instanceof FonctionClasseGroupe)) {
      return false;
    }
    final FonctionClasseGroupe other = (FonctionClasseGroupe) obj;
    if (this.classeGroupe == null) {
      if (other.classeGroupe != null) {
        return false;
      }
    } else if (!this.classeGroupe.equals(other.classeGroupe)) {
      return false;
    }
    if (this.type == null) {
      if (other.type != null) {
        return false;
      }
    } else if (!this.type.equals(other.type)) {
      return false;
    }
    return true;
  }

}
