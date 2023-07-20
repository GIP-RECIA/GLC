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
package fr.recia.glc.db.entities.relation;

import fr.recia.glc.db.entities.personne.Eleve;
import fr.recia.glc.db.entities.personne.PersonneRelationEleve;
import fr.recia.glc.db.enums.CategorieRelation;
import fr.recia.glc.db.enums.LienParente;
import fr.recia.glc.db.enums.ResponsableLegal;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@DiscriminatorValue(value = "PERSREL")
@Getter
@Setter
public class PersRelRelation extends AMappingRelation {

  /**
   * Type énuméré du type de lien de parenté.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I30)
  private LienParente lienParente;
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
  private boolean responsableFinancier;
  /**
   * Type énuméré du type de la relation de stage.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I15)
  private ResponsableLegal responsableLegal;
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
  private boolean contact;
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
  private boolean paiement;
  /* La relation est une adresse de domiciliation. */
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false")
  private boolean adresse;

  /**
   * Empty Constructor, must not be used.
   */
  public PersRelRelation() {
    super();
  }

  /**
   * @param source
   * @param persRelEleve
   * @param eleve
   * @param lienParente
   * @param responsableFinancier
   * @param responsableLegal
   * @param contact
   * @param paiement
   * @param adresse
   */
  public PersRelRelation(final String source, final PersonneRelationEleve persRelEleve, final Eleve eleve,
                         final LienParente lienParente, final boolean responsableFinancier,
                         final ResponsableLegal responsableLegal, final boolean contact,
                         final boolean paiement, final boolean adresse) {
    super(source, eleve, persRelEleve, CategorieRelation.PersRel);
    this.lienParente = lienParente;
    this.responsableFinancier = responsableFinancier;
    this.responsableLegal = responsableLegal;
    this.contact = contact;
    this.paiement = paiement;
    this.adresse = adresse;
  }

  @Override
  public String toString() {
    return "PersRelRelation [lienParente=" +
      this.lienParente + ", responsableFinancier=" +
      this.responsableFinancier + ", responsableLegal=" +
      this.responsableLegal + ", contact=" +
      this.contact + ", paiement=" +
      this.paiement + ", adresse=" +
      this.adresse + ", toString()=" +
      super.toString() +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + (adresse ? 1231 : 1237);
    result = prime * result + (contact ? 1231 : 1237);
    result = prime * result + ((lienParente == null) ? 0 : lienParente.hashCode());
    result = prime * result + (paiement ? 1231 : 1237);
    result = prime * result + (responsableFinancier ? 1231 : 1237);
    result = prime * result + ((responsableLegal == null) ? 0 : responsableLegal.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (!super.equals(obj))
      return false;
    if (getClass() != obj.getClass())
      return false;
    PersRelRelation other = (PersRelRelation) obj;
    if (contact != other.contact)
      return false;
    if (lienParente != other.lienParente)
      return false;
    if (paiement != other.paiement)
      return false;
    if (responsableFinancier != other.responsableFinancier)
      return false;
    if (responsableLegal != other.responsableLegal)
      return false;
    return true;
  }

}
