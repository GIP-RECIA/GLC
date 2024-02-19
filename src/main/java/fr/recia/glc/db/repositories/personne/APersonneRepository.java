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
package fr.recia.glc.db.repositories.personne;

import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface APersonneRepository<T extends APersonne> extends AbstractRepository<T, Long> {

  @Query("SELECT new fr.recia.glc.db.dto.personne.PersonneDto(ap.id, ap.etat, ap.anneeScolaire, ap.categorie," +
    "ap.civilite, ap.cleJointure.source, ap.cn, ap.dateNaissance, ap.email, ap.givenName, ap.patronyme, " +
    "ap.sn, ap.uid, l.nom, ap.structRattachement.id, ap.dateFin, ap.dateSourceModification, " +
    "ap.dateModification, ap.dateAcquittement) " +
    "FROM APersonne ap " +
    "LEFT JOIN Login l ON ap.login.id = l.id " +
    "WHERE ap.id = :id")
  PersonneDto findByPersonneId(Long id);

  @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
    "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
    "FROM APersonne ap " +
    "WHERE ap.id IN :ids " +
    "ORDER BY ap.cn, ap.sn")
  List<SimplePersonneDto> findByPersonneIds(Set<Long> ids);

  @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie," +
    "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
    "FROM APersonne ap " +
    "WHERE ap.id = :id")
  SimplePersonneDto findByPersonneIdSimple(Long id);

  @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie," +
    "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
    "FROM APersonne ap " +
    "WHERE ap.uid = :uid")
  SimplePersonneDto findByPersonneUid(String uid);

  @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
    "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
    "FROM APersonne ap " +
    "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.uid LIKE concat(:name, '%')) " +
    "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
    "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
    "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
    "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
    "ORDER BY ap.cn, ap.sn")
  List<SimplePersonneDto> findByNameLike(String name);

}
