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
import fr.recia.glc.db.entities.common.TypeService;
import fr.recia.glc.db.entities.education.CategorieDiscipline;
import fr.recia.glc.db.enums.CategoriePersonne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public abstract class APersonnel extends APersonne {

  /**
   * Relation bidirectionnelle.
   * Liste des catégories de disciplines de poste d'une personne (Enseignant, NonEnsEtab) au sein d'un établissement.
   */
  @Fetch(FetchMode.JOIN)
  @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinTable(
    name = "apersonnes_catdisciplines",
    foreignKey = @ForeignKey(name = "FK_APERSONNE_ID_CAT_DISCIPLINE_ID"),
    joinColumns = @JoinColumn(name = "APERSONNE_ID", referencedColumnName = "id", table = "apersonne"),
    inverseJoinColumns = @JoinColumn(name = "CATDISCIPLINE_ID", referencedColumnName = "id")
  )
  private Set<CategorieDiscipline> categorieDisciplines = new HashSet<>();
  /**
   * Relation bidirectionnelle.
   * Liste des services de la personne (NonEns Etab, NonEnsCollLoc, NonEnsServAc, Enseignant)
   */
  @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinTable(
    name = "apersonnes_services",
    foreignKey = @ForeignKey(name = "FK_APERSONNE_ID_SERVICE_ID"),
    joinColumns = @JoinColumn(name = "APERSONNE_ID", referencedColumnName = "id", table = "apersonne"),
    inverseJoinColumns = @JoinColumn(name = "SERVICE_ID", referencedColumnName = "id")
  )
  private Set<TypeService> services = new HashSet<>();

  public APersonnel(final Date anneeScolaire, final CategoriePersonne categorie, final CleJointure cleJointure,
                    final String cn, final String givenName, final String sn) {
    super(anneeScolaire, categorie, cleJointure, cn, givenName, sn);
  }

}
