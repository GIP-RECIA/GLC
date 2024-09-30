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

import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.CategorieFonction;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
@Getter
@Setter
public class Fonction extends AFonction {

  /**
   * Relation unidirectionnelle.
   * Discipline de poste d'un enseignant ou d'un personnel d'établissement.
   */
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "discipline_poste_fk")
  private Discipline disciplinePoste;

  /**
   * Relation unidirectionnelle.
   * Fonction filière, N_FONCTION_FILIERE.
   */
  @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
  @JoinColumn(name = "filiere_fk")
  private TypeFonctionFiliere filiere;

  /**
   * Relation unidirectionnelle.
   * Structure d'exercice de la fonction.
   */
  @ManyToOne
  @JoinColumn(name = "astructure_fk")
  private AStructure structure;

  /**
   * Constructeur de l'objet Fonction.java.
   */
  public Fonction() {
    super();
    this.setCategorie(CategorieFonction.Fonction);
  }

  /**
   * Constructeur de l'objet Fonction.java.
   *
   * @param filiere   Fonction filière, N_FONCTION_FILIERE.
   * @param structure Structure d'exercice de la fonction.
   * @param personne  Personne ayant cette fonction.
   * @param source    Source d'alimentation gérant cette fonction.
   */
  public Fonction(final TypeFonctionFiliere filiere, final AStructure structure,
                  final APersonne personne, final String source) {
    super(CategorieFonction.Fonction, personne, source);
    this.filiere = filiere;
    this.structure = structure;
  }

  /**
   * Constructeur de l'objet Fonction.java.
   *
   * @param filiere   Fonction filière, N_FONCTION_FILIERE.
   * @param structure Structure d'exercice de la fonction.
   * @param personne  Personne ayant cette fonction.
   * @param source    Source d'alimentation gérant cette fonction.
   * @param dateFin   Date de fin de la fonction.
   */
  public Fonction(final TypeFonctionFiliere filiere, final AStructure structure,
                  final APersonne personne, final String source, final Date dateFin) {
    super(CategorieFonction.Fonction, personne, source, dateFin);
    this.filiere = filiere;
    this.structure = structure;
  }

  /**
   * Constructeur de l'objet Fonction.java.
   *
   * @param disciplinePoste Discipline de poste d'un enseignant ou d'un personnel d'établissement.
   * @param filiere         Fonction filière, N_FONCTION_FILIERE.
   * @param structure       Structure d'exercice de la fonction.
   * @param personne        Personne ayant cette fonction.
   * @param source          Source d'alimentation gérant cette fonction.
   */
  public Fonction(final Discipline disciplinePoste, final TypeFonctionFiliere filiere,
                  final AStructure structure, final APersonne personne, final String source) {
    super(CategorieFonction.Fonction, personne, source);
    this.disciplinePoste = disciplinePoste;
    this.filiere = filiere;
    this.structure = structure;
  }

  /**
   * Constructeur de l'objet Fonction.java.
   *
   * @param disciplinePoste Discipline de poste d'un enseignant ou d'un personnel d'établissement.
   * @param filiere         Fonction filière, N_FONCTION_FILIERE.
   * @param structure       Structure d'exercice de la fonction.
   * @param personne        Personne ayant cette fonction.
   * @param source          Source d'alimentation gérant cette fonction.
   * @param dateFin         Date de fin de la fonction.
   */
  public Fonction(final Discipline disciplinePoste, final TypeFonctionFiliere filiere,
                  final AStructure structure, final APersonne personne, final String source, final Date dateFin) {
    super(CategorieFonction.Fonction, personne, source, dateFin);
    this.disciplinePoste = disciplinePoste;
    this.filiere = filiere;
    this.structure = structure;
  }

  /**
   * Transforme cette instance en chaine de caractères.
   *
   * @return <code>String</code> La chaine.
   * @see fr.recia.glc.db.entities.fonction.AFonction#toString()
   */
  @Override
  public String toString() {
    return "Fonction [" +
      super.toString() + ", " +
      this.disciplinePoste + ", " +
      this.filiere + ", " +
      this.structure +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    if (this.filiere == null) {
      result = prime * result;
    } else {
      result = prime * result + this.filiere.hashCode();
    }
    if (this.disciplinePoste == null) {
      result = prime * result;
    } else {
      result = prime * result + this.disciplinePoste.hashCode();
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
    if (!(obj instanceof Fonction)) {
      return false;
    }
    final Fonction other = (Fonction) obj;
    if (this.filiere == null) {
      if (other.filiere != null) {
        return false;
      }
    } else if (!this.filiere.equals(other.filiere)) {
      return false;
    }
    if (this.disciplinePoste == null) {
      if (other.disciplinePoste != null) {
        return false;
      }
    } else if (!this.disciplinePoste.equals(other.disciplinePoste)) {
      return false;
    }
    if (this.structure == null) {
      return other.structure == null;
    } else return this.structure.equals(other.structure);
  }

}
