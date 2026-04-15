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
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static fr.recia.glc.configuration.Constants.SANS_OBJET;
import static fr.recia.glc.utils.SourceUtils.areSourcesEquals;

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

    private static final String ALL = "ALL";
    private static final String FILIERE = "filieres";
    private static final String DISCIPLINE = "disciplines";

    private final List<CustomConfigProperties.FonctionsProperties> fonctionsProperties;

    public FonctionService(GLCProperties glcProperties) {
        this.fonctionsProperties = glcProperties.getCustomConfig().getFonctions();
    }

    @Cacheable(value = "fonctions")
    public List<Object> getFonctions() {
        log.trace("getFonctions");
        final List<String> sources = disciplineRepository.findAllNonSarapisSources();
        if (sources.isEmpty()) return Collections.emptyList();

        final List<TypeFonctionFiliereDto> typesFonctionFiliere = typeFonctionFiliereRepository.findWithoutSource();
        final List<DisciplineDto> disciplines = disciplineRepository.findWithoutSource();
        final List<FonctionDto> fonctions = fonctionRepository.findWithoutSource();

        final ArrayList<Object> data = new ArrayList<>();

        final Map<String, Object> all = new HashMap<>();
        all.put("source", ALL);
        all.put(FILIERE, getOfficial(
            typesFonctionFiliere,
            disciplines,
            new ArrayList<>(new LinkedHashSet<>(Stream.concat(
                fonctions.stream(),
                getFonctionsFromMapping(typesFonctionFiliere, disciplines).stream()
            ).collect(Collectors.toList()))),
            ALL));
        data.add(all);

        sources.forEach(source -> {
            final Map<String, Object> object = new HashMap<>();
            object.put("source", source);
            object.put(FILIERE, getOfficial(typesFonctionFiliere, disciplines, fonctions, source));
            object.put("customMapping", getCustomMapping(source));
            data.add(object);
        });

        return data;
    }

    private List<FonctionDto> getFonctionsFromMapping(
        List<TypeFonctionFiliereDto> typesFonctionFiliere,
        List<DisciplineDto> disciplines
    ) {
        List<FonctionDto> data = new ArrayList<>();

        fonctionsProperties.forEach(file -> file.getFilieres().forEach(filiere -> {
            TypeFonctionFiliereDto filiereDto = typesFonctionFiliere.stream()
                .filter(tffdto -> Objects.equals(tffdto.getCodeFiliere(), filiere.getCode()) && areSourcesEquals(tffdto.getSource(), file.getSource(), false))
                .findAny()
                .orElse(null);

            filiere.getDisciplines().forEach(discipline -> {
                DisciplineDto disciplineDto = disciplines.stream()
                    .filter(ddto -> Objects.equals(ddto.getCode(), discipline) && areSourcesEquals(ddto.getSource(), file.getSource(), false))
                    .findAny()
                    .orElse(null);

                if (disciplineDto != null && filiereDto != null)
                    data.add(new FonctionDto(disciplineDto.getId(), filiereDto.getId(), file.getSource()));
            });
        }));

        return data;
    }

    private List<TypeFonctionFiliereDto> getOfficial(
        List<TypeFonctionFiliereDto> typesFonctionFiliere,
        List<DisciplineDto> disciplines,
        List<FonctionDto> fonctions,
        String source
    ) {
        if (!source.equals(ALL)) {
            return getDisciplinesByFiliere(
                typesFonctionFiliere.stream()
                    .filter(typeFonctionFiliere -> areSourcesEquals(typeFonctionFiliere.getSource(), source))
                    .collect(Collectors.toList()),
                disciplines.stream()
                    .filter(discipline -> areSourcesEquals(discipline.getSource(), source))
                    .collect(Collectors.toList()),
                fonctions.stream()
                    .filter(fonction -> areSourcesEquals(fonction.getSource(), source, false))
                    .collect(Collectors.toList()),
                source
            ).stream()
                .filter(typeFonctionFiliere -> !typeFonctionFiliere.getDisciplines().isEmpty())
                .collect(Collectors.toList());
        }

        return getDisciplinesByFiliere(typesFonctionFiliere, disciplines, fonctions, source);
    }

    private Map<String, Object> getCustomMapping(String source) {
        Map<String, Object> data = new HashMap<>();

        // Recherche du mapping
        CustomConfigProperties.FonctionsProperties fonction = fonctionsProperties.stream()
            .filter(f -> Objects.equals(f.getSource(), source))
            .findAny()
            .orElse(null);
        if (fonction == null) return data;

        // Recherche des filières
        List<String> typeFonctionFiliereCodes = fonction.getFilieres().stream()
            .map(CustomConfigProperties.FonctionsProperties.FiliereProperties::getCode)
            .collect(Collectors.toList());
        List<TypeFonctionFiliereDto> typesFonctionFiliere =
            typeFonctionFiliereRepository.findByCodeAndSourceSarapis(typeFonctionFiliereCodes, source);
        typesFonctionFiliere.sort(Comparator.comparingInt(tff -> typeFonctionFiliereCodes.indexOf(tff.getCodeFiliere())));

        // Ajout des disciplines aux filières
        if (!typesFonctionFiliere.isEmpty()) {
            typesFonctionFiliere = typesFonctionFiliere.stream()
                .map(typeFonctionFiliere -> {
                    List<String> disciplineCodes = fonction.getFilieres().stream()
                        .filter(af -> Objects.equals(af.getCode(), typeFonctionFiliere.getCodeFiliere()))
                        .findAny()
                        .map(CustomConfigProperties.FonctionsProperties.FiliereProperties::getDisciplines)
                        .orElse(new ArrayList<>());

                    List<DisciplineDto> disciplines = disciplineRepository.findByCodeAndSourceSarapis(disciplineCodes, source);
                    typeFonctionFiliere.setDisciplines(new HashSet<>(disciplines));
                    return typeFonctionFiliere;
                })
                .collect(Collectors.toList());
            data.put(FILIERE, typesFonctionFiliere);
        }

        // Recherche des disciplines sans filières
        List<DisciplineDto> disciplines = disciplineRepository.findByCodeAndSourceSarapis(fonction.getDisciplines(), source);
        if (!disciplines.isEmpty()) data.put(DISCIPLINE, disciplines);

        return data;
    }

    private List<TypeFonctionFiliereDto> getDisciplinesByFiliere(
        List<TypeFonctionFiliereDto> typesFonctionFiliere,
        List<DisciplineDto> disciplines,
        List<FonctionDto> fonctions,
        String source
    ) {
        return getDisciplinesByFiliere(typesFonctionFiliere, disciplines, fonctions, source, List.of(SANS_OBJET), List.of(SANS_OBJET));
    }

    private List<TypeFonctionFiliereDto> getDisciplinesByFiliere(
        List<TypeFonctionFiliereDto> typesFonctionFiliere,
        List<DisciplineDto> disciplines,
        List<FonctionDto> fonctions,
        String source,
        List<String> typesFonctionFiliereToNotInclue,
        List<String> disciplinesToNotInclue
    ) {
        if (fonctions.isEmpty()) return Collections.emptyList();

        final List<TypeFonctionFiliereDto> tmpTypesFonctionFiliere = typesFonctionFiliere.stream()
            .map(TypeFonctionFiliereDto::new)
            .collect(Collectors.toList());
        final List<DisciplineDto> tmpDisciplines = disciplines.stream()
            .map(DisciplineDto::new)
            .collect(Collectors.toList());
        final List<FonctionDto> tmpFonctions = fonctions.stream()
            .map(FonctionDto::new)
            .collect(Collectors.toList());

        return tmpTypesFonctionFiliere.stream()
            .filter(typeFonctionFiliere -> !typesFonctionFiliereToNotInclue.contains(typeFonctionFiliere.getLibelleFiliere()))
            .map(typeFonctionFiliere -> {
                Set<Long> disciplineIds = tmpFonctions.stream()
                    .filter(fonction -> Objects.equals(fonction.getFiliere(), typeFonctionFiliere.getId()))
                    .map(FonctionDto::getDiscipline)
                    .collect(Collectors.toSet());
                List<DisciplineDto> disciplinesInFiliere = tmpDisciplines.stream()
                    .filter(discipline -> disciplineIds.contains(discipline.getId()) && !disciplinesToNotInclue.contains(discipline.getDisciplinePoste()))
                    .collect(Collectors.toList());
                typeFonctionFiliere.setDisciplines(new HashSet<>(disciplinesInFiliere));

                return typeFonctionFiliere;
            })
            .collect(Collectors.toList());
    }

    @Cacheable(value = "personneFonctions")
    public List<FonctionDto> getPersonneFonctions(Long personneId) {
        log.trace("getPersonneFonctions for {}", personneId);
        return fonctionRepository.findByPersonne(personneId);
    }

    public List<FonctionDto> getAdditionalFonctions(Long personneId) {
        return fonctionRepository.findByPersonne(personneId).stream()
            .filter(fonction -> fonction.getSource().startsWith(Constants.SARAPISUI_))
            .collect(Collectors.toList());
    }

    @Cacheable(value = "structureFonctions")
    public List<FonctionDto> getStructureFonctions(Long structureId) {
        log.trace("getStructureFonctions for {}", structureId);
        return fonctionRepository.findByStructureId(structureId);
    }

    @Cacheable(value = "typesFonctionFiliere")
    public List<TypeFonctionFiliereDto> getTypesFonctionFiliere(String source) {
        log.trace("getTypesFonctionFiliere for {}", source);
        return typeFonctionFiliereRepository.findBySourceSarapis(source);
    }

    @Cacheable(value = "disciplines")
    public List<DisciplineDto> getDisciplines(String source) {
        log.trace("getDisciplines for {}", source);
        return disciplineRepository.findBySourceSarapis(source);
    }

    @Cacheable(value = "personnesWithoutFunctions")
    public List<SimplePersonneDto> getPersonnesWithoutFunctions(Long structureId, boolean showUid) {
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
