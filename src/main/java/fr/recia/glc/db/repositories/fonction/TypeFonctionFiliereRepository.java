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
package fr.recia.glc.db.repositories.fonction;

import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeFonctionFiliereRepository<T extends TypeFonctionFiliere> extends AbstractRepository<T, Long> {

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto(tff.id, tff.codeFiliere, " +
    "tff.libelleFiliere, tff.source) " +
    "FROM TypeFonctionFiliere tff " +
    "WHERE tff.codeFiliere IN :codes " +
    "AND tff.source = :source " +
    "ORDER BY tff.libelleFiliere")
  List<TypeFonctionFiliereDto> findByCodeAndSource(List<String> codes, String source);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto(tff.id, tff.codeFiliere, " +
    "tff.libelleFiliere, tff.source) " +
    "FROM TypeFonctionFiliere tff " +
    "WHERE tff.codeFiliere IN :codes " +
    "AND (tff.source = :source " +
    "OR tff.source = CONCAT('SarapisUi_', :source)) " +
    "ORDER BY tff.libelleFiliere")
  List<TypeFonctionFiliereDto> findByCodeAndSourceSarapis(List<String> codes, String source);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto(tff.id, tff.codeFiliere, " +
    "tff.libelleFiliere, tff.source) " +
    "FROM TypeFonctionFiliere tff " +
    "WHERE tff.source = :source " +
    "ORDER BY tff.libelleFiliere")
  List<TypeFonctionFiliereDto> findBySource(String source);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto(tff.id, tff.codeFiliere, " +
    "tff.libelleFiliere, tff.source) " +
    "FROM TypeFonctionFiliere tff " +
    "WHERE tff.source = :source " +
    "OR tff.source = CONCAT('SarapisUi_', :source) " +
    "OR tff.source LIKE 'COLL-%' " +
    "ORDER BY tff.libelleFiliere")
  List<TypeFonctionFiliereDto> findBySourceSarapis(String source);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto(tff.id, tff.codeFiliere, " +
    "tff.libelleFiliere, tff.source) " +
    "FROM TypeFonctionFiliere tff " +
    "ORDER BY tff.libelleFiliere")
  List<TypeFonctionFiliereDto> findWithoutSource();

  @Query(value = "SELECT tff.id " +
    "FROM typefonctionfiliere tff " +
    "WHERE tff.codeFiliere = :code " +
    "AND (tff.source = :source " +
    "OR tff.source = CONCAT('SarapisUi_', :source))",
    nativeQuery = true)
  Long findByCode(String code, String source);

}
