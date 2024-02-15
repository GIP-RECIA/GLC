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

import fr.recia.glc.db.dto.education.DisciplineDto;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class TypeFonctionFiliereDto {

  private Long id;
  private String codeFiliere;
  private String libelleFiliere;
  private String source;
  private List<DisciplineDto> disciplines;

  public TypeFonctionFiliereDto(Long id, String codeFiliere, String libelleFiliere, String source) {
    this.id = id;
    this.codeFiliere = codeFiliere;
    this.libelleFiliere = libelleFiliere;
    this.source = source;
  }

  public TypeFonctionFiliereDto(TypeFonctionFiliereDto typeFonctionFiliereDto) {
    this.id = typeFonctionFiliereDto.getId();
    this.codeFiliere = typeFonctionFiliereDto.getCodeFiliere();
    this.libelleFiliere = typeFonctionFiliereDto.getLibelleFiliere();
    this.source = typeFonctionFiliereDto.getSource();
    if (typeFonctionFiliereDto.getDisciplines() != null)
      this.disciplines = typeFonctionFiliereDto.getDisciplines().stream().map(DisciplineDto::new).collect(Collectors.toList());
    else this.disciplines = null;
  }

}
