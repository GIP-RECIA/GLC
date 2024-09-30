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
package fr.recia.glc.db.entities.personne;

import fr.recia.glc.db.entities.common.CleJointure;
import fr.recia.glc.db.enums.CategoriePersonne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import java.util.Date;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PersonnelExterieur extends APersonnel {

  /**
   * Constructeur de l'objet PersonelExterieur.java.
   */
  public PersonnelExterieur() {
    super();
    this.setCategorie(CategoriePersonne.Personnel_exterieur);
  }

  /**
   * Constructeur de l'objet PersonelExterieur.java.
   *
   * @param anneeScolaire Année scolaire de validité de l'individu. Année à la rentrée de septembre.
   * @param cleJointure   Clé de jointure, identifiant unique fourni par les différentes sources, mais unique uniquement pour le périmètre de la
   *                      source.
   * @param cn            Nom canonique de la personne : NOM + Prénom usuels.
   * @param givenName     Prénom usuel.
   * @param sn            Nom d'usage.
   */
  public PersonnelExterieur(final Date anneeScolaire, final CleJointure cleJointure,
                            final String cn, final String givenName, final String sn) {
    super(anneeScolaire, CategoriePersonne.Personnel_exterieur, cleJointure, cn, givenName, sn);
  }

}
