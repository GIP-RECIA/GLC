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

import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FonctionRepository<T extends Fonction> extends AbstractRepository<T, Long> {

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.FonctionDto(f.disciplinePoste.id, f.filiere.id, f.source, " +
    "f.structure.id) " +
    "FROM Fonction f " +
    "WHERE f.personne.id = :id")
  List<FonctionDto> findByPersonne(Long id);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.FonctionDto(f.disciplinePoste.id, f.filiere.id, f.source) " +
    "FROM Fonction f " +
    "WHERE f.personne.id = :id " +
    "AND f.personne.structRattachement.id = :structure")
  List<FonctionDto> findByPersonneIdAndStructure(Long id, Long structure);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.FonctionDto(f.disciplinePoste.id, f.filiere.id, f.source) " +
    "FROM Fonction f " +
    "WHERE f.source = :source " +
    "AND f.disciplinePoste IS NOT NULL " +
    "AND f.filiere IS NOT NULL " +
    "AND f.categorie = 'Fonction'")
  List<FonctionDto> findBySource(String source);

  @Query("SELECT DISTINCT f.filiere.id " +
    "FROM Fonction f " +
    "WHERE f.structure.id = :id")
  List<Long> findFilieresByStructureId(Long id);

  @Query("SELECT new fr.recia.glc.db.dto.fonction.FonctionDto(f.personne.id, f.disciplinePoste.id, f.filiere.id, " +
    "f.source) " +
    "FROM Fonction f " +
    "WHERE f.structure.id = :structureId " +
    "AND (f.source = :source OR f.source = CONCAT('SarapisUi_', :source))")
  List<FonctionDto> findByStructureIdAndSource(Long structureId, String source);

}
