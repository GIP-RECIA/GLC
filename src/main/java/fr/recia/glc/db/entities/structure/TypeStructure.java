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

import fr.recia.glc.db.entities.common.AbstractSimpleEntity;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(uniqueConstraints = {
  @UniqueConstraint(columnNames = {"libelle", "sigle"})
})
@Getter
@Setter
public class TypeStructure extends AbstractSimpleEntity {

  /**
   * Nom complet du type.
   */
  @Column(length = IntConst.I80)
  private String libelle;
  /**
   * Nom court du type.
   */
  @Column(length = IntConst.I25)
  private String sigle;

  /**
   * Relation bidirectionnelle.
   * Liste des structures de ce type.
   */
  @OneToMany(mappedBy = "type", fetch = FetchType.LAZY)
  private Set<AStructure> structures = new HashSet<>();

  //Constructeurs

  /**
   * Constructeur de l'objet TypeStructure.java.
   */
  public TypeStructure() {
    super();
  }

  /**
   * Constructeur de l'objet TypeStructure.java.
   *
   * @param sigle   Nom court du type.
   * @param libelle Nom complet du type.
   */
  public TypeStructure(final String sigle, final String libelle) {
    super();
    this.sigle = sigle;
    this.libelle = libelle;
  }

  @Override
  public String toString() {
    return "TypeStructure [" +
      super.toString() + ", " +
      this.libelle + ", " +
      this.sigle +
      "]";
  }

}
