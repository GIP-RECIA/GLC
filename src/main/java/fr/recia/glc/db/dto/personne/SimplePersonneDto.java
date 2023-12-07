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
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
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

  public SimplePersonneDto(APersonne personne) {
    this.id = personne.getId();
    this.etat = personne.getEtat();
    this.categorie = personne.getCategorie();
    this.source = personne.getCleJointure().getSource();
    this.cn = personne.getCn();
    this.sn = personne.getSn();
    this.uid = personne.getUid();
  }

  public SimplePersonneDto(SimplePersonneDto simplePersonneDto) {
    this.id = simplePersonneDto.getId();
    this.etat = simplePersonneDto.getEtat();
    this.categorie = simplePersonneDto.getCategorie();
    this.source = simplePersonneDto.getSource();
    this.cn = simplePersonneDto.getCn();
    this.email = simplePersonneDto.getEmail();
    this.sn = simplePersonneDto.getSn();
    this.uid = simplePersonneDto.getEmail();
  }

}
