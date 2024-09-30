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
package fr.recia.glc.db.entities.common;

import fr.recia.glc.db.enums.ExternalIdSource;
import fr.recia.glc.db.utils.IntConst;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
@Getter
@Setter
public class ExternalId {

  /**
   * Identifiant externe.
   */
  @Column(nullable = false)
  private String id;
  /**
   * Destinataire de l'identifiant.
   */
  @Enumerated(EnumType.STRING)
  @Column(length = IntConst.I20, nullable = false)
  private ExternalIdSource destinataire;

  /**
   * Constructeur de l'objet ExternalId.java.
   */
  public ExternalId() {
    super();
  }

  /**
   * Constructeur de l'objet Mail.java.
   *
   * @param id           Identifiant.
   * @param destinataire Le destinataire utilisant l'identifiant.
   */
  public ExternalId(final String id, final ExternalIdSource destinataire) {
    super();
    this.id = id;
    this.destinataire = destinataire;
  }

  @Override
  public String toString() {
    return "ExternalId [id=" +
      this.id + ", destinataire=" +
      this.destinataire +
      "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((destinataire == null) ? 0 : destinataire.hashCode());
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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
    ExternalId other = (ExternalId) obj;
    if (destinataire != other.destinataire)
      return false;
    if (id == null) {
      return other.id == null;
    } else return id.equals(other.id);
  }

}
