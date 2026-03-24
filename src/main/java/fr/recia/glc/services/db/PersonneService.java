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

import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.APersonneAStructure;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class PersonneService {

    @Autowired
    private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private FonctionRepository<Fonction> fonctionRepository;

    public List<SimplePersonneDto> searchPersonne(String name, boolean admin) {
        return admin ? aPersonneRepository.findByNameLikeAdmin(name) : aPersonneRepository.findByNameLike(name);
    }

    @Cacheable(value = "personnesByEtablissement")
    public List<SimplePersonneDto> getPersonnes(Long structureId, boolean showUid) {
        log.trace("getPersonnes for {}", structureId);
        final List<Long> personnesIds = aPersonneAStructureRepository.findPersonneByStructureId(structureId);
        if (personnesIds.isEmpty()) return List.of();
        if(showUid){
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

    public SimplePersonneDto getPersonneSimple(Long id) {
        return aPersonneRepository.findByPersonneIdSimple(id);
    }

    public boolean isInStructure(Long id, Long structureId) {
        return aPersonneAStructureRepository.isInStructure(id, structureId) > 0;
    }

    public boolean hasOfficialFunctionsInStructure(Long id, Long structureId) {
        return fonctionRepository.nbOfficialFonctionsInStructure(id, structureId) > 0;
    }

    public boolean hasAdditionalFunctionsInStructure(Long id, Long structureId) {
        return fonctionRepository.nbAdditionalFonctionsInStructure(id, structureId) > 0;
    }

    public boolean hasAdditionalFunctionsInStructure(Long id, Long structureId, List<Long> fonctionsIds) {
        return fonctionRepository.nbAdditionalFonctionsInStructure(id, structureId, fonctionsIds) > 0;
    }

    public boolean hasFunctionsInStructure(Long id, Long structureId, List<Long> fonctionsIds) {
        return this.hasOfficialFunctionsInStructure(id, structureId) ||
                this.hasAdditionalFunctionsInStructure(id, structureId, fonctionsIds);
    }

}
