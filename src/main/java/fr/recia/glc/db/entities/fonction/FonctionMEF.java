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
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.enums.CategorieFonction;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
public class FonctionMEF extends AFonction {

  /**
   * Relation unidirectionnelle.
   */
  @ManyToOne
  @JoinColumn(name = "etablissement_fk")
  private Etablissement etablissement;

  /**
   * Relation unidirectionnelle.
   */
  @ElementCollection(fetch = FetchType.LAZY)
  @JoinTable(
    name = "fonctions_mefs",
    joinColumns = @JoinColumn(name = "FONCTIONMEF_ID", referencedColumnName = "ID")
  )
  private Set<MappingFonctionMEFMEF> mefs = new HashSet<>();

  /**
   * Constructeur de l'objet FonctionMEF.java.
   */
  public FonctionMEF() {
    super();
    this.setCategorie(CategorieFonction.MEF);
  }

  /**
   * Constructeur de l'objet FonctionMEF.java.
   *
   * @param mefs          Liste des mefs.
   * @param etablissement l'établissement associé à l'enseignement de ces mef
   * @param personne      la personne ayant la fonction d'enseigner associé à ce mefs.
   * @param source        Source d'alimentation gérant cette fonction.
   */
  public FonctionMEF(final Set<MappingFonctionMEFMEF> mefs, final Etablissement etablissement,
                     final APersonne personne, final String source) {
    super(CategorieFonction.MEF, personne, source);
    this.mefs = mefs;
    this.etablissement = etablissement;
  }

  @Override
  public String toString() {
    return "FonctionMEF [" +
      super.toString() + ", " +
      this.etablissement + ", " +
      this.mefs +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    if (this.etablissement == null) {
      result = prime * result;
    } else {
      result = prime * result + this.etablissement.hashCode();
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
    if (!(obj instanceof FonctionMEF)) {
      return false;
    }
    final FonctionMEF other = (FonctionMEF) obj;
    if (this.etablissement == null) {
      if (other.etablissement != null) {
        return false;
      }
    } else if (!this.etablissement.equals(other.etablissement)) {
      return false;
    }
    return true;
  }

}
