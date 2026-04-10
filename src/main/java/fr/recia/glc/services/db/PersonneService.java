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
package fr.recia.glc.services.db;

import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.APersonneAStructure;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.ldap.repository.LdapPeopleDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class PersonneService {

    @Autowired
    private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private LdapPeopleDao ldapPeopleDao;

    public List<SimplePersonneDto> searchPersonne(String name, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdmin(name) : aPersonneRepository.findByNameLike(name);
    }

    public List<SimplePersonneDto> searchPersonneInStaffCategories(String name, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInStaffCategories(name) : aPersonneRepository.findByNameLikeInStaffCategories(name);
    }

    public List<SimplePersonneDto> searchPersonneInEtab(String name, Set<Long> structureIds, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInEtab(name, structureIds) : aPersonneRepository.findByNameLikeInEtab(name, structureIds);
    }

    public List<SimplePersonneDto> searchPersonneInEtabInStaffCategories(String name, Set<Long> structureIds, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInEtabInStaffCategories(name, structureIds) : aPersonneRepository.findByNameLikeInEtabInStaffCategories(name, structureIds);
    }

    public List<SimplePersonneDto> searchPersonneInEtabBySiren(String name, Set<String> sirens, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInEtabBySiren(name, sirens) : aPersonneRepository.findByNameLikeInEtabBySiren(name, sirens);
    }

    public List<SimplePersonneDto> searchPersonneInEtabBySirenInStaffCategories(String name, Set<String> sirens, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminInEtabBySirenInStaffCategories(name, sirens) : aPersonneRepository.findByNameLikeInEtabBySirenInStaffCategories(name, sirens);
    }

    public List<SimplePersonneDto> searchPersonneNotInEtab(String name, Set<Long> structureIds, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminNotInEtab(name, structureIds) : aPersonneRepository.findByNameLikeNotInEtab(name, structureIds);
    }

    public List<SimplePersonneDto> searchPersonneNotInEtabInStaffCategories(String name, Set<Long> structureIds, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminNotInEtabInStaffCategories(name, structureIds) : aPersonneRepository.findByNameLikeNotInEtabInStaffCategories(name, structureIds);
    }

    public List<SimplePersonneDto> searchPersonneNotInEtabButInSiren(String name, Set<Long> structureIds, Set<String> sirens, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminNotInEtabButInSiren(name, structureIds, sirens) : aPersonneRepository.findByNameLikeNotInEtabButInSiren(name, structureIds, sirens);
    }

    public List<SimplePersonneDto> searchPersonneNotInEtabButInSirenInStaffCategories(String name, Set<Long> structureIds, Set<String> sirens, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdminNotInEtabButInSirenInStaffCategories(name, structureIds, sirens) : aPersonneRepository.findByNameLikeNotInEtabButInSirenInStaffCategories(name, structureIds, sirens);
    }

    @Cacheable(value = "personnesByEtablissement")
    public List<SimplePersonneDto> getPersonnes(Long structureId, boolean showUid) {
        log.trace("getPersonnes for {}", structureId);
        final List<Long> personnesIds = aPersonneAStructureRepository.findPersonneByStructureId(structureId);
        if (personnesIds.isEmpty()) return List.of();
        if (showUid) {
            return aPersonneRepository.findByPersonneIdsWithUid(new HashSet<>(personnesIds));
        } else {
            return aPersonneRepository.findByPersonneIdsWithoutUid(new HashSet<>(personnesIds));
        }
    }

    @Cacheable(value = "personne")
    public APersonne getPersonne(Long id) {
        log.trace("getPersonne for {}", id);
        return aPersonneRepository.findById(id).orElse(null);
    }

    /**
     * Bloque une personne : attention il faut bloquer dans la BD et dans le LDAP aussi
     */
    // TODO : evict correctement le cache de la personne et de l'établissement pour le tableau de bord
    public boolean lockPerson(APersonne aPersonne){
        if(aPersonne.getEtat() != Etat.Bloque){
            aPersonne.setEtat(Etat.Bloque);
            aPersonneRepository.saveAndFlush(aPersonne);
            ldapPeopleDao.lockPerson(aPersonne.getUid());
            return true;
        } else {
            log.warn("Try to lock person {} that is already locked", aPersonne.getId());
        }
        return false;
    }

    /**
     * Débloque une personne : attention il faut débloquer dans la BD et dans le LDAP aussi
     */
    // TODO : evict correctement le cache de la personne et de l'établissement pour le tableau de bord
    public boolean unlockPerson(APersonne aPersonne){
        // TODO : dans quel état remettre la personne on ne sait pas dans quel état elle était avant
        if(aPersonne.getEtat() == Etat.Bloque){
            aPersonne.setEtat(Etat.Valide);
            aPersonneRepository.saveAndFlush(aPersonne);
            ldapPeopleDao.unlockPerson(aPersonne.getUid());
            return true;
        } else {
            log.warn("Try to unlock person {} that is not locked", aPersonne.getId());
        }
        return false;
    }

    public SimplePersonneDto getPersonneSimple(Long id) {
        return aPersonneRepository.findByPersonneIdSimple(id);
    }

}
