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
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FonctionRepository<T extends Fonction> extends AbstractRepository<T, Long> {

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.FonctionDto(f.disciplinePoste.id, f.filiere.id, f.source, " +
    "f.structure.id, f.dateFin) " +
    "FROM Fonction f " +
    "WHERE f.personne.id = :id " +
    "ORDER BY f.filiere.libelleFiliere")
  List<FonctionDto> findByPersonne(Long id);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.FonctionDto(f.disciplinePoste.id, f.filiere.id, f.source) " +
    "FROM Fonction f " +
    "WHERE f.personne.id = :id " +
    "AND f.personne.structRattachement.id = :structure " +
    "ORDER BY f.filiere.libelleFiliere")
  List<FonctionDto> findByPersonneIdAndStructure(Long id, Long structure);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.FonctionDto(f.disciplinePoste.id, f.filiere.id, f.source) " +
    "FROM Fonction f " +
    "WHERE f.source = :source OR f.source = CONCAT('SarapisUi_', :source) " +
    "AND f.disciplinePoste IS NOT NULL " +
    "AND f.filiere IS NOT NULL " +
    "AND f.categorie = 'Fonction' " +
    "ORDER BY f.filiere.libelleFiliere")
  List<FonctionDto> findBySource(String source);

  @Query("SELECT DISTINCT new fr.recia.glc.db.dto.fonction.FonctionDto(f.disciplinePoste.id, f.filiere.id, f.source) " +
    "FROM Fonction f " +
    "WHERE f.disciplinePoste IS NOT NULL " +
    "AND f.filiere IS NOT NULL " +
    "AND f.categorie = 'Fonction' " +
    "ORDER BY f.filiere.libelleFiliere")
  List<FonctionDto> findWithoutSource();

  @Query(value = "select distinct apas.APERSONNE_ID " +
    "from apersonnes_astructures apas " +
    "inner join apersonne ap on ap.id = apas.APERSONNE_ID " +
    "where apas.ASTRUCTURE_ID = :structureId " +
    "and (" +
    "apas.APERSONNE_ID not in (" +
    "select distinct af.personne_fk " +
    "from afonction af " +
    "inner join fonction f on f.id = af.id " +
    "inner join apersonne ap on ap.id = af.personne_fk " +
    "where f.astructure_fk = :structureId" +
    ") " +
    "or apas.APERSONNE_ID not in (" +
    "select distinct af.personne_fk " +
    "from afonction af " +
    "inner join fonction f on af.id = f.id " +
    "inner join typefonctionfiliere tff on f.filiere_fk = tff.id " +
    "inner join discipline d on f.discipline_poste_fk = d.id " +
    "where f.astructure_fk = :structureId " +
    "and (tff.codeFiliere != '-' or d.code != '-') " +
    "group by af.personne_fk " +
    "having count(f.filiere_fk) > 0" +
    ") " +
    ") " +
    "and ap.categorie in ('Enseignant', 'Non_enseignant_collectivite_locale', 'Non_enseignant_etablissement', 'Non_enseignant_service_academique')",
    nativeQuery = true)
  List<Long> findPersonnesWithoutFunctions(Long structureId);

  @Query("SELECT DISTINCT f.filiere.id " +
    "FROM Fonction f " +
    "WHERE f.structure.id = :id")
  List<Long> findFilieresByStructureId(Long id);

  @Query("SELECT new fr.recia.glc.db.dto.fonction.FonctionDto(f.personne.id, f.disciplinePoste.id, f.filiere.id, " +
    "f.source) " +
    "FROM Fonction f " +
    "WHERE f.structure.id = :structureId " +
    "ORDER BY f.filiere.libelleFiliere")
  List<FonctionDto> findByStructureId(Long structureId);

  @Query("SELECT f.id " +
    "FROM Fonction f " +
    "WHERE f.disciplinePoste.id = :disciplineId " +
    "AND f.filiere.id = :filiereId " +
    "AND f.personne.id = :personneId " +
    "AND f.structure.id = :structureId")
  Long findId(Long disciplineId, Long filiereId, Long personneId, Long structureId);

  @Query(value = "select count(af.id) " +
    "from afonction af " +
    "inner join fonction f on f.id = af.id " +
    "inner join discipline d on f.discipline_poste_fk = d.id " +
    "where f.astructure_fk = :structureId " +
    "and d.code = :disciplineCode",
    nativeQuery = true)
  Long nbDiscipline(Long structureId, String disciplineCode);

}
