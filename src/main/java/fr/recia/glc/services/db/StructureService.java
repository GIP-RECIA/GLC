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

import fr.recia.glc.db.dto.structure.StructureDto;
import fr.recia.glc.db.dto.structure.SimpleStructureDto;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.QAStructure;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.web.dto.function.DisciplineDisplayDto;
import fr.recia.glc.web.dto.function.DisciplinePossibleDto;
import fr.recia.glc.web.dto.function.FiliereDisplayDto;
import fr.recia.glc.web.dto.function.FonctionDisplayDto;
import fr.recia.glc.web.dto.function.FonctionPossibleDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IteratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StructureService {

    @Autowired
    private AStructureRepository<AStructure> structureRepository;

    @Autowired
    private FonctionRepository<Fonction> fonctionRepository;

    @Cacheable(value = "stuctureDBById")
    public AStructure getStructureDBFromId(Long id){
        return structureRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "etablissement")
    public StructureDto getStructureDTOFromId(Long id) {
        log.trace("getEtablissement for {}", id);
        AStructure aStructure = structureRepository.findById(id).orElse(null);
        return aStructure != null ? new StructureDto(aStructure) : null;
    }

    // TODO : ne retourner que les établissements
    public List<SimpleStructureDto> getEtablissements(Set<String> allowedSiren) {
        return IteratorUtils.toList(
                structureRepository.findAll(QAStructure.aStructure.siren.isNotNull().and(QAStructure.aStructure.siren.in(allowedSiren))).iterator()).stream()
            .map(SimpleStructureDto::new)
            .sorted(Comparator.comparing(SimpleStructureDto::getNom))
            .collect(Collectors.toList());
    }

    public List<SimpleStructureDto> getStructures(Set<String> allowedSiren) {
        return IteratorUtils.toList(
                structureRepository.findAll(QAStructure.aStructure.siren.isNotNull().and(QAStructure.aStructure.siren.in(allowedSiren))).iterator()).stream()
            .map(SimpleStructureDto::new)
            .sorted(Comparator.comparing(SimpleStructureDto::getNom))
            .collect(Collectors.toList());
    }

    public Map<Long, DisciplinePossibleDto> getPossibleFonctions(String source){
        List<FonctionPossibleDto> fonctionPossibleDtos = fonctionRepository.findPossibleFonctionsBySource(source);
        Map<Long, DisciplinePossibleDto> dtoListMap = new HashMap<>();
        for(FonctionPossibleDto fonctionPossibleDto : fonctionPossibleDtos){
            FiliereDisplayDto filiereDisplayDto = fonctionPossibleDto.getFiliere();
            if(!dtoListMap.containsKey(filiereDisplayDto.getId())){
                dtoListMap.put(filiereDisplayDto.getId(), new DisciplinePossibleDto(filiereDisplayDto.getLibelle()));
            }
            dtoListMap.get(filiereDisplayDto.getId()).getDisciplines().add(fonctionPossibleDto.getDiscipline());
        }
        return dtoListMap;
    }
}
