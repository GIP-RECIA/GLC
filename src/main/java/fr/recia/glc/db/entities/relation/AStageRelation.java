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

import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.personne.Eleve;
import fr.recia.glc.db.enums.CategorieRelation;
import fr.recia.glc.db.enums.TypeStage;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Entity
@Getter
@Setter
public abstract class AStageRelation extends AMappingRelation {

  /**
   * Type énuméré du type de la relation de stage.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I30)
  private TypeStage type;

  /**
   * Empty Constructor, must not be used.
   */
  public AStageRelation() {
    super();
  }

  /**
   * @param source
   * @param tuteurStage
   * @param eleve
   * @param type
   * @param typeStage
   */
  public AStageRelation(final String source, final APersonne tuteurStage, final Eleve eleve,
                        final TypeStage type, final CategorieRelation typeStage) {
    super(source, tuteurStage, eleve, typeStage);
    this.type = type;
  }

  @Override
  public String toString() {
    return "StageRelation [" +
      type + ", " +
      super.toString() +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((type == null) ? 0 : type.hashCode());
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
    AStageRelation other = (AStageRelation) obj;
    return type == other.type;
  }

}
