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

import fr.recia.glc.db.dto.structure.EtablissementDto;
import fr.recia.glc.db.dto.structure.SimpleStructureDto;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.entities.structure.QEtablissement;
import fr.recia.glc.db.repositories.structure.EtablissementRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EtablissementService {

    @Autowired
    private EtablissementRepository<Etablissement> etablissementRepository;

    public List<SimpleStructureDto> getEtablissements() {
        return IteratorUtils.toList(
                etablissementRepository
                    .findAll(QEtablissement.etablissement.uai.isNotNull(), Sort.by("nom"))
                    .iterator()
            ).stream()
            .map(SimpleStructureDto::new)
            .collect(Collectors.toList());
    }

    public List<SimpleStructureDto> getEtablissements(Set<String> allowedUAI) {
        return IteratorUtils.toList(
                etablissementRepository
                    .findAll(QEtablissement.etablissement.uai.isNotNull().and(QEtablissement.etablissement.uai.in(allowedUAI)),
                        Sort.by("nom")
                    ).iterator()
            ).stream()
            .map(SimpleStructureDto::new)
            .collect(Collectors.toList());
    }

    @Cacheable(value = "etablissement")
    public EtablissementDto getEtablissement(Long id) {
        log.trace("getEtablissement for {}", id);
        Etablissement etablissement = etablissementRepository.findById(id).orElse(null);
        return etablissement != null ? new EtablissementDto(etablissement) : null;
    }

}
