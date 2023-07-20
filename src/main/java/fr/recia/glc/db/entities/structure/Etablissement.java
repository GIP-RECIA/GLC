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
package fr.recia.glc.db.entities.structure;

import fr.recia.glc.db.entities.common.CleJointure;
import fr.recia.glc.db.entities.groupe.GroupementEtablissements;
import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.db.enums.Contrat;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class Etablissement extends AStructure {

  /**
   * Numero UAI, ancien RNE.
   */
  @Column(unique = true, length = IntConst.IUAI)
  private String uai;
  /**
   * Type de contrat (public, privé, non défini).
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I30)
  private Contrat contrat;

  /**
   * Relation unidirectionnelle.
   * Le ministère de tutelle.
   */
  @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  @JoinColumn(name = "ministere_tutelle_fk")
  private MinistereTutelle ministereTutelle;
  /**
   * Relation unidirectionnelle.
   * Le bassin de formation.
   */
  @ManyToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY)
  @JoinColumn(name = "bassin_formation_fk")
  private BassinFormation bassinFormation;
  /**
   * Relation unidirectionnelle.
   * La Liste des structures de rattachement administratif.
   */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinTable(
    name = "etab_rattachement_administratif",
    joinColumns = @JoinColumn(name = "ETABLISSEMENT_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "ASTRUCTURE_ID", referencedColumnName = "ID")
  )
  private Set<AStructure> rattachementAdministratif = new HashSet<>();
  /**
   * Relation unidirectionnelle.
   * La Liste des structures de rattachement fonctionnel.
   */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
  @JoinTable(
    name = "etab_rattachement_fonctionnel",
    joinColumns = @JoinColumn(name = "ETABLISSEMENT_ID", referencedColumnName = "ID"),
    inverseJoinColumns = @JoinColumn(name = "ASTRUCTURE_ID", referencedColumnName = "ID")
  )
  private Set<AStructure> rattachementFonctionnel = new HashSet<>();
  /**
   * Relation bidirectionnelle.
   * Liste des groupes d'établissement dont l'établissement fait parti.
   */
  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY, mappedBy = "membres")
  private Set<GroupementEtablissements> groupements = new HashSet<>();

  /**
   * Constructeur de l'objet Etablissement.java.
   */
  public Etablissement() {
    super();
    this.setCategorie(CategorieStructure.Etablissement);
  }

  /**
   * Constructeur de l'objet Etablissement.java.
   *
   * @param uai              Numero UAI, ancien RNE.
   * @param nom              Nom unique de la stucture.
   * @param siren            Numéro de SIRET/SIREN unique de la structure.
   * @param cleJointure      Clé de jointure unique de la structure.
   * @param ministereTutelle Ministère de tutelle.
   * @param contrat          Type de contrat (public, privé, non défini).
   */
  public Etablissement(final String uai, final String nom, final String siren, final CleJointure cleJointure,
                       final MinistereTutelle ministereTutelle, final Contrat contrat) {
    super(CategorieStructure.Etablissement, nom, siren, cleJointure);
    this.uai = uai;
    this.ministereTutelle = ministereTutelle;
    this.contrat = contrat;
  }

  @Override
  public String toString() {
    return "Etablissement [" +
      super.toString() + ", " +
      this.uai + ", " +
      this.contrat + ", " +
      this.bassinFormation + ", " +
      this.ministereTutelle + ", " +
      this.groupements +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    if (this.uai == null) {
      result = prime * result;
    } else {
      result = prime * result + this.uai.hashCode();
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
    if (!(obj instanceof Etablissement)) {
      return false;
    }
    final Etablissement other = (Etablissement) obj;
    if (this.uai == null) {
      if (other.uai != null) {
        return false;
      }
    } else if (!this.uai.equals(other.uai)) {
      return false;
    }
    return true;
  }

}
