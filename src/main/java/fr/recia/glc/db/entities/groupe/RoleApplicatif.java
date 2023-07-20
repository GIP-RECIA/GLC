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

import fr.recia.glc.db.entities.application.Application;
import fr.recia.glc.db.enums.CategorieGroupe;
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
public class RoleApplicatif extends AGroupeOfAPersonne {

  /**
   * Relation unidirectionnelle.
   * Liste des profils utiles au rôle applicatif.
   */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
  @JoinTable(name = "roles_applicatifs_profils",
    joinColumns = @JoinColumn(name = "ROLEAPPLICATIF_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "PROFIL_ID", referencedColumnName = "ID")
  )
  private Set<Profil> profils = new HashSet<>();
  /**
   * Relation bidirectionnelle.
   * Application pour laquelle le role applicatif est défini.
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "APPLICATION_ID")
  private Application proprietaire;

  /**
   * Constructeur de l'objet RoleApplicatif.java.
   */
  public RoleApplicatif() {
    super();
    this.setCategorie(CategorieGroupe.Role_applicatif);
  }

  /**
   * Constructeur de l'objet RoleApplicatif.java.
   *
   * @param cn          Nom unique de groupe, peut servir comme identifiant.
   * @param membres     Liste des membres du groupe en dehors des profils.
   * @param application Application pour laquelle le role applicatif est défini.
   * @param source      Source ayant créé l'objet.
   */
  public RoleApplicatif(final String cn, final Set<MappingAGroupeAPersonne> membres,
                        final Application application, final String source) {
    super(cn, CategorieGroupe.Role_applicatif, membres, source);
    this.proprietaire = application;
  }

  @Override
  public String toString() {
    return "RoleApplicatif [" +
      super.toString() + ", " +
      this.profils +
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

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof RoleApplicatif)) {
      return false;
    }
    final RoleApplicatif other = (RoleApplicatif) obj;
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
