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
package fr.recia.glc.db.repositories.education;

import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisciplineRepository<T extends Discipline> extends AbstractRepository<T, Long> {

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.education.DisciplineDto(d.id, d.code, d.disciplinePoste, d.source) " +
    "FROM Discipline d " +
    "WHERE d.code IN :codes " +
    "AND d.source = :source " +
    "ORDER BY d.disciplinePoste")
  List<DisciplineDto> findByCodeAndSource(List<String> codes, String source);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.education.DisciplineDto(d.id, d.code, d.disciplinePoste, d.source) " +
    "FROM Discipline d " +
    "WHERE d.code IN :codes " +
    "AND (d.source = :source " +
    "OR d.source = CONCAT('SarapisUi_', :source)) " +
    "ORDER BY d.disciplinePoste")
  List<DisciplineDto> findByCodeAndSourceSarapis(List<String> codes, String source);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.education.DisciplineDto(d.id, d.code, d.disciplinePoste, d.source) " +
    "FROM Discipline d " +
    "WHERE d.source = :source " +
    "ORDER BY d.disciplinePoste")
  List<DisciplineDto> findBySource(String source);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.education.DisciplineDto(d.id, d.code, d.disciplinePoste, d.source) " +
    "FROM Discipline d " +
    "WHERE d.source = :source " +
    "OR d.source = CONCAT('SarapisUi_', :source) " +
    "ORDER BY d.disciplinePoste")
  List<DisciplineDto> findBySourceSarapis(String source);

  @Query("SELECT DISTINCT d.source " +
    "FROM Discipline d " +
    "WHERE d.source NOT LIKE 'SarapisUi_%'" +
    "ORDER BY d.source")
  List<String> findAllNonSarapisSources();

}
