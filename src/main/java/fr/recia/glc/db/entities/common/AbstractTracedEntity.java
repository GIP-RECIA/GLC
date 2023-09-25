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

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@ToString(callSuper = true)
public abstract class AbstractTracedEntity extends AbstractAutoGeneratedIdEntity implements Serializable {

  /**
   * Gestion des versions de l'objet, incrémentation de +1 à chaque modification de type update ou merge.
   */
  @Version
  private long version;
  /**
   * Création automatique de la date de création de l'objet lors de la construction.
   */
  @CreatedDate
  @NotNull
  @Column(nullable = false)
  private Instant dateCreation = Instant.now();
  /**
   * Donne l'information de la date de modification de l'objet.
   */
  @LastModifiedDate
  private Instant dateModification = Instant.now();
  /**
   * Donne l'information de la date d'acquittement de l'objet lors de l'export.
   */
  private Instant dateAcquittement;

  /**
   * Constructeur de l'objet AbstractTracedEntity.java.
   */
  public AbstractTracedEntity() {
    super();
  }

}
