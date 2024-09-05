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
  private Long filiere;
  private Long discipline;
  private String source;
  private Long structure;
  private Date dateFin;

  public FonctionDto(Long filiere, Long discipline, String source) {
    this.filiere = filiere;
    this.discipline = discipline;
    this.source = source;
  }

  public FonctionDto(Long filiere, Long discipline, String source, Long structure) {
    this.filiere = filiere;
    this.discipline = discipline;
    this.source = source;
    this.structure = structure;
  }

  public FonctionDto(Long filiere, Long discipline, String source, Long structure, Date dateFin) {
    this.filiere = filiere;
    this.discipline = discipline;
    this.source = source;
    this.structure = structure;
    this.dateFin = dateFin;
  }

  public FonctionDto(Long personne, Long filiere, Long discipline, String source) {
    this.personne = personne;
    this.filiere = filiere;
    this.discipline = discipline;
    this.source = source;
  }

  public FonctionDto(FonctionDto fonctionDto) {
    this.personne = fonctionDto.getPersonne();
    this.discipline = fonctionDto.getDiscipline();
    this.filiere = fonctionDto.getFiliere();
    this.source = fonctionDto.getSource();
    this.structure = fonctionDto.getStructure();
    this.dateFin = fonctionDto.getDateFin();
  }

}
