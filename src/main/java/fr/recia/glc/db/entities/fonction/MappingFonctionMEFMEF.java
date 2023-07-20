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

import fr.recia.glc.db.entities.education.MEF;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Parent;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Embeddable
@Getter
@Setter
public class MappingFonctionMEFMEF {

  /**
   * Entity parent fonctionMEF
   */
  @Parent
  @Column(name = "FONCTIONMEF_ID", nullable = false)
  private FonctionMEF fonctionMEF;
  /**
   * Source d'alimentation de l'association.
   */
  @Basic
  @Column(length = IntConst.ISOURCE, name = "SOURCE", nullable = false)
  private String source;
  /**
   * Si l'enseignant est responsable de formation.
   */
  @Basic
  @Column(nullable = false, columnDefinition = "BIT not null DEFAULT false", name = "RESPONSABLE")
  private boolean isResponsable;
  /**
   * Relation avec le MEF.
   */
  @OneToOne
  @JoinColumn(name = "MEF_ID", nullable = false)
  private MEF mef;

  /**
   * Contructor of the object MappingFonctionMEFMEF.java.
   */
  public MappingFonctionMEFMEF() {
    super();
  }

  /**
   * Contructor of the object MappingFonctionMEFMEF.java.
   *
   * @param source
   * @param mef
   * @param isResponsable
   */
  public MappingFonctionMEFMEF(final String source, final MEF mef, final boolean isResponsable) {
    super();
    this.source = source;
    this.mef = mef;
    this.isResponsable = isResponsable;
  }

  @Override
  public String toString() {
    return "MappingFonctionMEFMEF [" +
      this.source + ", " +
      this.mef.toString() +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    if (this.mef == null) {
      result = prime * result;
    } else {
      result = prime * result + mef.hashCode();
    }
    if (this.fonctionMEF == null) {
      result = prime * result;
    } else {
      result = prime * result + fonctionMEF.hashCode();
    }
    if (this.source == null) {
      result = prime * result;
    } else {
      result = prime * result + this.source.hashCode();
    }
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MappingFonctionMEFMEF other = (MappingFonctionMEFMEF) obj;
    if (mef == null) {
      if (other.mef != null)
        return false;
    } else if (!mef.equals(other.mef))
      return false;
    if (source == null) {
      if (other.source != null)
        return false;
    } else if (!source.equals(other.source))
      return false;
    return true;
  }

}
