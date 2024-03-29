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
package fr.recia.glc.db.dto.education;

import fr.recia.glc.db.dto.personne.SimplePersonneDto;
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
public class DisciplineDto {

  private Long id;
  private String code;
  private String disciplinePoste;
  private String source;
  private List<SimplePersonneDto> personnes;

  public DisciplineDto(Long id, String code, String disciplinePoste, String source) {
    this.id = id;
    this.code = code;
    this.disciplinePoste = disciplinePoste;
    this.source = source;
  }

  public DisciplineDto(DisciplineDto disciplineDto) {
    this.id = disciplineDto.getId();
    this.code = disciplineDto.getCode();
    this.disciplinePoste = disciplineDto.getDisciplinePoste();
    this.source = disciplineDto.getSource();
    if (disciplineDto.getPersonnes() != null)
      this.personnes = disciplineDto.getPersonnes().stream().map(SimplePersonneDto::new).collect(Collectors.toList());
    else this.personnes = null;
  }

}
