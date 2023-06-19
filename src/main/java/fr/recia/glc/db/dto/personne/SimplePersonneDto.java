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

import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Etat;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class SimplePersonneDto {

  private Long id;
  private Etat etat;
  private CategoriePersonne categorie;
  private String givenName;
  private String patronyme;
  private List<FonctionDto> fonctions;

  public SimplePersonneDto(Long id, Etat etat, CategoriePersonne categorie, String givenName, String patronyme) {
    this.id = id;
    this.etat = etat;
    this.categorie = categorie;
    this.givenName = givenName;
    this.patronyme = patronyme;
  }

}