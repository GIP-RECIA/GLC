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

import fr.recia.glc.configuration.GLCProperties;
import fr.recia.glc.configuration.bean.CustomConfigProperties;
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.dto.structure.StructureDto;
import fr.recia.glc.db.dto.structure.SimpleStructureDto;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.entities.structure.QAStructure;
import fr.recia.glc.db.entities.structure.QEtablissement;
import fr.recia.glc.db.repositories.education.DisciplineRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.db.repositories.structure.EtablissementRepository;
import fr.recia.glc.web.dto.function.DisciplinePossibleDto;
import fr.recia.glc.web.dto.function.DisciplinesInFillierePossiblesDto;
import fr.recia.glc.web.dto.function.FiliereDisplayDto;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class StructureService {

    @Autowired
    private AStructureRepository<AStructure> structureRepository;

    @Autowired
    private EtablissementRepository<Etablissement> etablissementRepository;

    @Autowired
    private FonctionRepository<Fonction> fonctionRepository;

    @Autowired
    private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;

    @Autowired
    private DisciplineRepository<Discipline> disciplineRepository;

    @Autowired
    private GLCProperties glcProperties;

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

    public List<SimpleStructureDto> getEtablissements(Set<String> allowedSiren) {
        return IteratorUtils.toList(etablissementRepository.findAll(QEtablissement.etablissement.siren.isNotNull().and(QEtablissement.etablissement.siren.in(allowedSiren))).iterator()).stream()
            .map(SimpleStructureDto::new)
            .sorted(Comparator.comparing(SimpleStructureDto::getNom))
            .collect(Collectors.toList());
    }

    public List<SimpleStructureDto> getStructures(Set<String> allowedSiren) {
        return IteratorUtils.toList(structureRepository.findAll(QAStructure.aStructure.siren.isNotNull().and(QAStructure.aStructure.siren.in(allowedSiren))).iterator()).stream()
            .map(SimpleStructureDto::new)
            .sorted(Comparator.comparing(SimpleStructureDto::getNom))
            .collect(Collectors.toList());
    }

    // TODO : ajouter aussi celles du mapping custom
    public List<DisciplinesInFillierePossiblesDto> getPossibleFonctions(String source){
        List<FonctionPossibleDto> fonctionPossibleDtos = fonctionRepository.findPossibleFonctionsBySource(source);
        Map<Long, DisciplinesInFillierePossiblesDto> dtoListMap = new HashMap<>();

        // Fonctions déjà existantes sur cette source
        for(FonctionPossibleDto fonctionPossibleDto : fonctionPossibleDtos){
            FiliereDisplayDto filiereDisplayDto = fonctionPossibleDto.getFiliere();
            if(!dtoListMap.containsKey(filiereDisplayDto.getId())){
                dtoListMap.put(filiereDisplayDto.getId(), new DisciplinesInFillierePossiblesDto(filiereDisplayDto.getId(), filiereDisplayDto.getLibelle()));
            }
            dtoListMap.get(filiereDisplayDto.getId()).getDisciplines().add(fonctionPossibleDto.getDiscipline());
        }

        // Fonctions ajoutées via le mapping custom
        CustomConfigProperties.FonctionsProperties fonctionsProperties = glcProperties.getCustomConfig().getFonctions().stream()
            .filter(f -> Objects.equals(f.getSource(), source))
            .findAny()
            .orElse(null);

        // TODO : filières sans disciplines, pour ça il faut modifier la structure du DisciplinesInFillierePossiblesDto
        if(fonctionsProperties != null){
            for(CustomConfigProperties.FonctionsProperties.FiliereProperties filiereProperties : fonctionsProperties.getFilieres()){
                TypeFonctionFiliereDto typeFonctionFiliere = typeFonctionFiliereRepository.findByCodeAndSourceSarapis(filiereProperties.getCode(), source);
                for(String disciplineCode : filiereProperties.getDisciplines()){
                    DisciplineDto discipline = disciplineRepository.findByCodeAndSourceSarapis(disciplineCode, source);
                    if(!dtoListMap.containsKey(typeFonctionFiliere.getId())){
                        dtoListMap.put(typeFonctionFiliere.getId(), new DisciplinesInFillierePossiblesDto(typeFonctionFiliere.getId(), typeFonctionFiliere.getLibelleFiliere()));
                    }
                    dtoListMap.get(typeFonctionFiliere.getId()).getDisciplines().add(new DisciplinePossibleDto(discipline.getId(), disciplineCode));
                }
            }
        }

        return new ArrayList<>(dtoListMap.values());
    }
}
