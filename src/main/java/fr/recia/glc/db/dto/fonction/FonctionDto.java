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
package fr.recia.glc.db.dto.fonction;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FonctionDto {

  private Long personne;
  private Long disciplinePoste;
  private Long filiere;
  private String source;
  private Long structure;
  private Date dateFin;

  public FonctionDto(Long disciplinePoste, Long filiere, String source) {
    this.disciplinePoste = disciplinePoste;
    this.filiere = filiere;
    this.source = source;
  }

  public FonctionDto(Long disciplinePoste, Long filiere, String source, Long structure) {
    this.disciplinePoste = disciplinePoste;
    this.filiere = filiere;
    this.source = source;
    this.structure = structure;
  }

  public FonctionDto(Long disciplinePoste, Long filiere, String source, Long structure, Date dateFin) {
    this.disciplinePoste = disciplinePoste;
    this.filiere = filiere;
    this.source = source;
    this.structure = structure;
    this.dateFin = dateFin;
  }

  public FonctionDto(Long personne, Long disciplinePoste, Long filiere, String source) {
    this.personne = personne;
    this.disciplinePoste = disciplinePoste;
    this.filiere = filiere;
    this.source = source;
  }

}
