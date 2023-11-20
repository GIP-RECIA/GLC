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
package fr.recia.glc.db.repositories.structure;

import fr.recia.glc.db.dto.structure.EtablissementDto;
import fr.recia.glc.db.dto.structure.SimpleEtablissementDto;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EtablissementRepository<T extends Etablissement> extends AbstractRepository<T, Long> {

  @Query("SELECT new fr.recia.glc.db.dto.structure.SimpleEtablissementDto(e.id, e.uai, e.categorie, e.nom, e.nomCourt, " +
    "e.siren) " +
    "FROM Etablissement e " +
    "WHERE e.uai IS NOT NULL " +
    "ORDER BY e.nom")
  List<SimpleEtablissementDto> findAllEtablissements();

  @Query("SELECT new fr.recia.glc.db.dto.structure.SimpleEtablissementDto(e.id, e.uai, e.categorie, e.nom, e.nomCourt, " +
    "e.siren) " +
    "FROM Etablissement e " +
    "WHERE e.uai IS NOT NULL AND e.uai IN (:uai) " +
    "ORDER BY e.nom")
  List<SimpleEtablissementDto> findAllowedEtablissements(Set<String> uai);

  @Query("SELECT new fr.recia.glc.db.dto.structure.EtablissementDto(e.id, e.uai, e.etat, e.etatAlim, " +
    "e.cleJointure.source, e.anneeScolaire, e.adresse.adresse , " +
    "e.adresse.codePostal , e.adresse.ville, e.adresse.boitePostale, e.adresse.pays, e.categorie, e.mail, e.nom, " +
    "e.nomCourt, e.siren, e.siteWeb, e.modeleLogin, e.logo) " +
    "FROM Etablissement e " +
    "WHERE e.id = :id")
  EtablissementDto findByEtablissementId(Long id);

}
