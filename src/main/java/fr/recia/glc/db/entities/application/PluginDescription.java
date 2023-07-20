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
package fr.recia.glc.db.entities.application;

import fr.recia.glc.db.enums.CategorieApplication;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@Setter
public class PluginDescription extends Application {

  /**
   * Nom de la classe à executer.
   */
  private String nomClass;
  /**
   * Nom du fichier de paramètres.
   */
  private String nomIni;
  /**
   * Nom du plugin.
   */
  private String nomPlugin;

  /**
   * Relation unidirectionnelle.
   */
  @ManyToOne
  @JoinColumn(name = "application_fk")
  private Application application;

  /**
   * Constructeur de l'objet PluginDescription.java.
   */
  public PluginDescription() {
    super();
  }

  /**
   * Constructeur de l'objet PluginDescription.java.
   *
   * @param identifiant Identifiant unique de l'application.
   * @param password    Password de l'application.
   * @param nom         Nom unique de l'application, pouvant servir de login.
   * @param categorie   Catégorie de l'application.
   */
  public PluginDescription(final String identifiant, final String password, final String nom,
                           final CategorieApplication categorie) {
    super(identifiant, password, nom, categorie);
  }

  /**
   * Constructeur de l'objet PluginDescription.java.
   *
   * @param identifiant Identifiant unique de l'application.
   * @param password    Password de l'application.
   * @param nom         Nom unique de l'application, pouvant servir de login.
   * @param categorie   Catégorie de l'application.
   * @param nomClass    Nom de la classe à executer.
   * @param application Application référente du plugin.
   */
  public PluginDescription(final String identifiant, final String password, final String nom,
                           final CategorieApplication categorie, final String nomClass, final Application application) {
    super(identifiant, password, nom, categorie);
    this.nomClass = nomClass;
    this.application = application;
  }

  @Override
  public String toString() {
    return "Plugin [" +
      super.toString() + ", " +
      this.nomClass + ", " +
      this.application.toString() + ", " +
      this.nomIni + ", " +
      this.nomPlugin +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.application == null) {
      result = prime * result;
    } else {
      result = prime * result + this.application.hashCode();
    }
    if (this.nomClass == null) {
      result = prime * result;
    } else {
      result = prime * result + this.nomClass.hashCode();
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
    if (!(obj instanceof PluginDescription)) {
      return false;
    }
    final PluginDescription other = (PluginDescription) obj;
    if (this.application == null) {
      if (other.application != null) {
        return false;
      }
    } else if (!this.application.equals(other.application)) {
      return false;
    }
    if (this.nomClass == null) {
      if (other.nomClass != null) {
        return false;
      }
    } else if (!this.nomClass.equals(other.nomClass)) {
      return false;
    }
    return true;
  }

}
