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
package fr.recia.glc.db.dto.personne;

import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Etat;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode
public class SimplePersonneDto {

  private Long id;
  private Etat etat;
  private CategoriePersonne categorie;
  private String source;
  private String cn;
  private String email;
  private String sn;
  private String uid;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant dateModification;
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private Instant dateAcquittement;

  public SimplePersonneDto(
    Long id, Etat etat, CategoriePersonne categorie, String source, String cn, String email, String sn, String uid,
    Instant dateModification, Instant dateAcquittement
  ) {
    this.id = id;
    this.etat = etat;
    this.categorie = categorie;
    this.source = source;
    this.cn = cn;
    this.email = email;
    this.sn = sn;
    this.uid = uid;

    if (etat == Etat.Delete && dateModification.equals(dateAcquittement))
      this.etat = Etat.Deleting;
  }

  public SimplePersonneDto(APersonne aPersonne) {
    this.id = aPersonne.getId();
    this.etat = aPersonne.getEtat();
    this.categorie = aPersonne.getCategorie();
    this.source = aPersonne.getCleJointure().getSource();
    this.cn = aPersonne.getCn();
    this.sn = aPersonne.getSn();
    this.uid = aPersonne.getUid();

    if (aPersonne.getEtat() == Etat.Delete && aPersonne.getDateModification().equals(aPersonne.getDateAcquittement()))
      this.etat = Etat.Deleting;
  }

  public SimplePersonneDto(SimplePersonneDto simplePersonneDto) {
    this.id = simplePersonneDto.getId();
    this.etat = simplePersonneDto.getEtat();
    this.categorie = simplePersonneDto.getCategorie();
    this.source = simplePersonneDto.getSource();
    this.cn = simplePersonneDto.getCn();
    this.email = simplePersonneDto.getEmail();
    this.sn = simplePersonneDto.getSn();
    this.uid = simplePersonneDto.getUid();
  }

}
