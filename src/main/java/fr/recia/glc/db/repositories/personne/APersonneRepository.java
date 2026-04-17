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

import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.repositories.AbstractRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface APersonneRepository<T extends APersonne> extends AbstractRepository<T, Long> {

    @Query("SELECT count(*) " +
        "FROM APersonne ap " +
        "WHERE ap.email = :email")
    Long doesEmailExists(String email);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.givenName, ap.sn, ap.login.nom, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "WHERE ap.id IN :ids " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByPersonneIdsWithUid(Set<Long> ids);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.givenName, ap.sn, ap.login.nom, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "WHERE ap.id IN :ids " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByPersonneIdsWithoutUid(Set<Long> ids);

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
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLike(String name);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdmin(String name);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND s.id IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeInEtab(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND s.id NOT IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeNotInEtab(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeInEtabBySiren(String name, Set<String> sirens);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND s.id NOT IN :etabId " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeNotInEtabButInSiren(String name, Set<Long> etabId, Set<String> sirens);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeInStaffCategories(String name);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeInEtabInStaffCategories(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id NOT IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeNotInEtabInStaffCategories(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeInEtabBySirenInStaffCategories(String name, Set<String> sirens);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') OR ap.email LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id NOT IN :etabId " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeNotInEtabButInSirenInStaffCategories(String name, Set<Long> etabId, Set<String> sirens);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND s.id IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminInEtab(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND s.id NOT IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminNotInEtab(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminInEtabBySiren(String name, Set<String> sirens);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND s.id NOT IN :etabId " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminNotInEtabButInSiren(String name, Set<Long> etabId, Set<String> sirens);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminInStaffCategories(String name);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminInEtabInStaffCategories(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id NOT IN :etabId " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminNotInEtabInStaffCategories(String name, Set<Long> etabId);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminInEtabBySirenInStaffCategories(String name, Set<String> sirens);

    @Query("SELECT new fr.recia.glc.db.dto.personne.SimplePersonneDto(ap.id, ap.etat, ap.categorie, " +
        "ap.cleJointure.source, ap.cn, ap.email, ap.sn, ap.uid, ap.dateModification, ap.dateAcquittement) " +
        "FROM APersonne ap " +
        "JOIN ap.listeStructures s "+
        "WHERE (ap.cn LIKE concat('%', :name, '%') " +
        "OR ap.email LIKE concat(:name, '%') " +
        "OR ap.uid LIKE concat(:name, '%')) " +
        "AND ap.categorie in (fr.recia.glc.db.enums.CategoriePersonne.Enseignant, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_collectivite_locale, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_etablissement, " +
        "fr.recia.glc.db.enums.CategoriePersonne.Non_enseignant_service_academique) " +
        "AND s.id NOT IN :etabId " +
        "AND s.siren IN :sirens " +
        "ORDER BY ap.cn, ap.sn")
    List<SimplePersonneDto> findByNameLikeAdminNotInEtabButInSirenInStaffCategories(String name, Set<Long> etabId, Set<String> sirens);

}
