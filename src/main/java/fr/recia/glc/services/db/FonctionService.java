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

import fr.recia.glc.configuration.Constants;
import fr.recia.glc.configuration.GLCProperties;
import fr.recia.glc.configuration.bean.CustomConfigProperties;
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.dto.personne.DatabasePersonneDto;
import fr.recia.glc.db.entities.APersonneAStructure;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.fonction.QFonction;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.APersonneAStructureRepository2;
import fr.recia.glc.db.repositories.education.DisciplineRepository;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.services.cache.CacheInvalidationService;
import fr.recia.glc.utils.SourceUtils;
import fr.recia.glc.web.dto.function.FonctionAction;
import fr.recia.glc.web.dto.function.FonctionToModify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FonctionService {

    @Autowired
    private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
    @Autowired
    private APersonneAStructureRepository2 aPersonneAStructureRepository2;
    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private AStructureRepository<AStructure> aStructureRepository;
    @Autowired
    private FonctionRepository<Fonction> fonctionRepository;
    @Autowired
    private DisciplineRepository<Discipline> disciplineRepository;
    @Autowired
    private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;
    @Autowired
    private GLCProperties glcProperties;
    @Autowired
    private CacheInvalidationService cacheInvalidationService;

    @Cacheable(value = "typeFonctionFiliere")
    public TypeFonctionFiliere getTypeFonctionFiliere(Long id){
        return typeFonctionFiliereRepository.getReferenceById(id);
    }

    @Cacheable(value = "discipline")
    public Discipline getDiscipline(Long id){
        return disciplineRepository.getReferenceById(id);
    }

    @Cacheable(value = "personneFonctions")
    public List<FonctionDto> getPersonneFonctions(Long personneId) {
        log.trace("getPersonneFonctions for {}", personneId);
        return fonctionRepository.findByPersonne(personneId);
    }

    @Cacheable(value = "structureFonctions")
    public List<FonctionDto> getStructureFonctions(Long structureId) {
        log.trace("getStructureFonctions for {}", structureId);
        return fonctionRepository.findByStructureId(structureId);
    }

    @Cacheable(value = "typesFonctionFiliere")
    public List<TypeFonctionFiliereDto> getTypesFonctionFiliere(String source) {
        log.trace("getTypesFonctionFiliere for {}", source);
        return typeFonctionFiliereRepository.findByAnySource();
    }

    @Cacheable(value = "disciplines")
    public List<DisciplineDto> getDisciplines(String source) {
        log.trace("getDisciplines for {}", source);
        return disciplineRepository.findByAnySource();
    }

    @Cacheable(value = "personnesWithoutFunctions")
    public List<DatabasePersonneDto> getPersonnesWithoutFunctions(Long structureId, boolean showUid) {
        log.trace("getPersonnesWithoutFunctions for {}", structureId);
        final List<Long> personnesIds = fonctionRepository.findPersonnesWithoutFunctions(structureId);
        if (showUid) {
            return aPersonneRepository.findByPersonneIdsWithUid(new HashSet<>(personnesIds));
        } else {
            return aPersonneRepository.findByPersonneIdsWithoutUid(new HashSet<>(personnesIds));
        }
    }

    /**
     * Modifie les fonctions d'une personne sur une stucture
     *
     * @param personneId        L'id de la personne
     * @param structureId       L'id de la stucture
     * @param toAddFunctions    La liste des fonctions à ajouter sous la forme "filière-discipline"
     * @param toDeleteFunctions La liste des fonctions à supprimer sous la forme "filière-discipline"
     * @param requiredAction    Si on doit rattacher une personne à un établissement (attach) ou le détacher (detach)
     */
    public boolean saveAdditionalFonctions(Long personneId, Long structureId, List<FonctionToModify> toAddFunctions, List<FonctionToModify> toDeleteFunctions, FonctionAction requiredAction, boolean isAdmin) {
        final APersonne aPersonne = aPersonneRepository.findById(personneId).orElse(null);
        final AStructure aStructure = aStructureRepository.findById(structureId).orElse(null);
        if (aPersonne == null
            || aStructure == null
            || !List.of(Etat.Invalide, Etat.Valide, Etat.Bloque).contains(aPersonne.getEtat())
        ) return false;

        final String sourceOrig = SourceUtils.getOfficialSource(aStructure.getCleJointure().getSource());
        final String source = Constants.SARAPISUI_ + sourceOrig;

        List<FonctionDto> toAddAdditional = new ArrayList<>();
        for (FonctionToModify fonctionToAdd : toAddFunctions) {
            // TODO : probleme dans la conf ce sont des codes alors qu'ici ce sont des ids
            TypeFonctionFiliere typeFonctionFiliere = typeFonctionFiliereRepository.findById(fonctionToAdd.getFiliere()).get();
            if(glcProperties.getCustomConfig().getAdminFonctionsBySource().get(sourceOrig).contains(typeFonctionFiliere.getCodeFiliere())){
                log.debug("Admin fonction : check user rights");
                if(isAdmin){
                    toAddAdditional.add(new FonctionDto(personneId, fonctionToAdd.getFiliere(), fonctionToAdd.getDiscipline(), source, structureId, fonctionToAdd.getDateFin()));
                } else {
                    log.warn("Can't add filiere {} because user is not authorized", typeFonctionFiliere.getLibelleFiliere());
                }
            } else {
                toAddAdditional.add(new FonctionDto(personneId, fonctionToAdd.getFiliere(), fonctionToAdd.getDiscipline(), source, structureId, fonctionToAdd.getDateFin()));
            }
        }

        List<FonctionDto> toDeleteAdditional = new ArrayList<>();
        for (FonctionToModify fonctionToDelete : toDeleteFunctions) {
            // TODO : probleme dans la conf ce sont des codes alors qu'ici ce sont des ids
            TypeFonctionFiliere typeFonctionFiliere = typeFonctionFiliereRepository.findById(fonctionToDelete.getFiliere()).get();
            if(glcProperties.getCustomConfig().getAdminFonctionsBySource().get(sourceOrig).contains(typeFonctionFiliere.getCodeFiliere())){
                log.debug("Admin fonction : check user rights");
                if(isAdmin){
                    toDeleteAdditional.add(new FonctionDto(personneId, fonctionToDelete.getFiliere(), fonctionToDelete.getDiscipline(), source, structureId));
                } else {
                    log.warn("Can't remove filiere {} because user is not authorized", typeFonctionFiliere.getLibelleFiliere());
                }
            } else {
                toDeleteAdditional.add(new FonctionDto(personneId, fonctionToDelete.getFiliere(), fonctionToDelete.getDiscipline(), source, structureId));
            }

        }

        if (!toAddAdditional.isEmpty()) {
            List<Fonction> fonctions = new ArrayList<>();
            for(FonctionDto fonctionDto : toAddAdditional){
                // Si on a déjà la fonction qu'on veut ajouter alors il ne faut pas la remettre ça va poser des problèmes de contraintes
                if(fonctionRepository.nbSameFonctionsInStructure(fonctionDto.getPersonne(), fonctionDto.getStructure(), fonctionDto.getDiscipline(), fonctionDto.getFiliere()) == 0){
                    TypeFonctionFiliere filiere = typeFonctionFiliereRepository.findById(fonctionDto.getFiliere()).orElse(null);
                    Discipline discipline = disciplineRepository.findById(fonctionDto.getDiscipline()).orElse(null);
                    fonctions.add(new Fonction(discipline, filiere, aStructure, aPersonne, source, fonctionDto.getDateFin()));
                } else {
                    log.warn("Try to add function {}-{} but already added for user {} in etab {}", fonctionDto.getFiliere(), fonctionDto.getDiscipline(), fonctionDto.getPersonne(), fonctionDto.getStructure());
                }
            }
            fonctionRepository.saveAll(fonctions);
        }

        if (!toDeleteAdditional.isEmpty()) {
            fonctionRepository.deleteAllById(toDeleteAdditional.stream()
                .map(fonction -> fonctionRepository.findId(fonction.getFiliere(), fonction.getDiscipline(), personneId, fonction.getStructure(), fonction.getSource()))
                .collect(Collectors.toList()));
        }

        if (!toAddAdditional.isEmpty() || !toDeleteAdditional.isEmpty()) {
            fonctionRepository.flush();

            boolean isInStructure = aPersonneAStructureRepository.isInStructure(personneId, structureId) > 0;
            int officialFonctionsInStructure = (int) fonctionRepository.findByPersonne(personneId).stream()
                .filter(fonction -> !SourceUtils.isSourceOfficial(fonction.getSource()) && Objects.equals(fonction.getStructure(), structureId))
                .count();

            switch (requiredAction) {
                case attach:
                    if (isInStructure || toAddAdditional.isEmpty()) {
                        log.error("Unable to attach user {} to structure {}", aPersonne.getId(), aStructure.getId());
                        break;
                    }
                    aPersonneAStructureRepository2.insertInStructure(personneId, structureId);
                    break;
                case detach:
                    if (!isInStructure || !toAddAdditional.isEmpty() || toDeleteAdditional.isEmpty() || officialFonctionsInStructure > 0) {
                        log.error("Unable to detach user {} to structure {}", aPersonne.getId(), aStructure.getId());
                        break;
                    }
                    aPersonneAStructureRepository2.deleteFromStructure(personneId, structureId);
                    break;
                default:
                    break;
            }
            aPersonneRepository.saveAndFlush(aPersonne);
        }

        // Vider les caches
        cacheInvalidationService.evictPersonneAndAssociatedStructures(personneId, structureId);

        return true;
    }

    public long nbDiscipline(Long structureId, String filiereCode, String disciplineCode) {
        return fonctionRepository.count(QFonction.fonction.structure.id.eq(structureId)
            .and(QFonction.fonction.filiere.codeFiliere.eq(filiereCode))
            .and(QFonction.fonction.disciplinePoste.code.eq(disciplineCode)));
    }

}
