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
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.APersonneAStructure;
import fr.recia.glc.db.entities.common.ExternalId;
import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.ExternalIdSource;
import fr.recia.glc.db.enums.ForceEtat;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.education.DisciplineRepository;
import fr.recia.glc.db.repositories.fonction.TypeFonctionFiliereRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.ldap.LdapUser;
import fr.recia.glc.ldap.repository.LdapPeopleDao;
import fr.recia.glc.services.cache.CacheInvalidationService;
import fr.recia.glc.web.dto.function.FonctionDisplayDto;
import fr.recia.glc.web.dto.user.StructureForUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class PersonneService {

    @Autowired
    private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
    @Autowired
    private APersonneRepository<APersonne> aPersonneRepository;
    @Autowired
    private LdapPeopleDao ldapPeopleDao;
    @Autowired
    private CacheInvalidationService cacheInvalidationService;
    @Autowired
    private GroupeService groupeService;
    @Autowired
    private FonctionService fonctionService;
    @Autowired
    private RelationService relationService;
    @Autowired
    private GLCProperties glcProperties;
    @Autowired
    private TypeFonctionFiliereRepository<TypeFonctionFiliere> typeFonctionFiliereRepository;
    @Autowired
    private DisciplineRepository<Discipline> disciplineRepository;


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
    public boolean lockPerson(APersonne aPersonne){
        if(aPersonne.getEtat().equals(Etat.Valide)){
            aPersonne.setEtat(Etat.Bloque);
            aPersonneRepository.saveAndFlush(aPersonne);
            ldapPeopleDao.lockPerson(aPersonne.getUid());
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        } else {
            log.warn("Try to lock person {} that is already locked", aPersonne.getId());
        }
        return false;
    }

    /**
     * Débloque une personne : attention il faut débloquer dans la BD et dans le LDAP aussi
     */
    public boolean unlockPerson(APersonne aPersonne){
        if(aPersonne.getEtat().equals(Etat.Bloque)){
            aPersonne.setEtat(Etat.Valide);
            aPersonneRepository.saveAndFlush(aPersonne);
            ldapPeopleDao.unlockPerson(aPersonne.getUid());
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        } else {
            log.warn("Try to unlock person {} that is not locked", aPersonne.getId());
        }
        return false;
    }

    /**
     * Permet de forcer la suppression d'un compte déjà en suppression
     * Le compte sera supprimé au prochain passage de sarapis
     */
    public boolean forceDelete(APersonne aPersonne){
        if(aPersonne.getEtat().equals(Etat.Delete) && aPersonne.getDateModification().equals(aPersonne.getDateAcquittement())){
            aPersonne.setForceEtat(ForceEtat.Deleted);
            Date date = new Date();
            aPersonne.setDateModification(date);
            aPersonneRepository.saveAndFlush(aPersonne);
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        }
        log.warn("Person {} is not in delete state or is already deleted", aPersonne.getId());
        return false;
    }

    /**
     * Permet de passer un compte en suppression
     * Le compte passera en suppression au prochain passage de sarapis
     */
    public boolean putInDeleteState(APersonne aPersonne){
        if(!aPersonne.getEtat().equals(Etat.Delete) && !ForceEtat.Deleted.equals(aPersonne.getForceEtat())){
            ldapPeopleDao.putInDeleteState(aPersonne.getUid());
            aPersonne.setEtat(Etat.Delete);
            Date date = new Date();
            aPersonne.setDateAcquittement(date);
            aPersonne.setDateModification(date);
            aPersonneRepository.saveAndFlush(aPersonne);
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        }
        log.warn("Person {} is already in delete state", aPersonne.getId());
        return false;
    }

    /**
     * Permet de retirer un compte de la supression (temporairement)
     */
    public boolean undoDelete(APersonne aPersonne){
        // On vérifie que la personne est en suppression et pas déjà supprimée
        if(aPersonne.getEtat().equals(Etat.Delete) && aPersonne.getDateAcquittement().equals(aPersonne.getDateModification())){
            Etat etatToRestore = Etat.Invalide;
            if(aPersonne.getValidationCharte() != null){
                etatToRestore = Etat.Valide;
            }
            ldapPeopleDao.undoDelete(aPersonne.getUid());
            aPersonne.setEtat(etatToRestore);
            LocalDate localDate = LocalDate.now().plusDays(14);
            aPersonne.setDateFin(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
            Date date = new Date();
            aPersonne.setDateAcquittement(date);
            aPersonne.setDateModification(date);
            aPersonneRepository.saveAndFlush(aPersonne);
            cacheInvalidationService.evictPersonneAndAssociatedStructures(aPersonne.getId(), aPersonne.getStructRattachement().getId());
            return true;
        }
        log.warn("Person {} is not in delete state or is already deleted", aPersonne.getId());
        return false;
    }

    public PersonneDto getFullPersonne(Long id, APersonne personne, boolean showUid, Set<String> allowedSirens){
        PersonneDto personneDto = new PersonneDto(personne, showUid);
        LdapUser ldapUser = ldapPeopleDao.getLdapUser(personne.getUid(), id);
        String sirenCourant = ldapUser.getSirenCourant();
        for(AStructure aStructure : personne.getListeStructures()){
            StructureForUserDto structureForUserDto = new StructureForUserDto(aStructure);
            if(allowedSirens.contains(aStructure.getSiren())){
                structureForUserDto.setAuthorizedForPrincipal(true);
            }
            personneDto.getListeStructures().add(structureForUserDto);
            if(aStructure.getId()==personne.getStructRattachement().getId()){
                structureForUserDto.setStructureRattachement(true);
            }
            if(aStructure.getSiren().equals(sirenCourant)){
                structureForUserDto.setStructureCourante(true);
            }
            structureForUserDto.setClasses(groupeService.getClassesOfPersonne(personne.getId(), personne.getCategorie(), aStructure.getId()));
            structureForUserDto.setGroupesPedagogiques(groupeService.getGroupesOfPersonne(personne.getId(), personne.getCategorie(), aStructure.getId()));
        }
        // Affichage de l'id pronote uniquement si la personne est dans un groupe pronote
        List<String> groups = ldapUser.getGroups();
        Pattern patternPronoteGroup = Pattern.compile(glcProperties.getCustomConfig().getPronoteGroupRegex());
        for(String group : groups){
            Matcher matcherPronoteGroup = patternPronoteGroup.matcher(group);
            if(matcherPronoteGroup.matches()){
                for(ExternalId externalId : personne.getExternalIds()){
                    if(externalId.getDestinataire().equals(ExternalIdSource.PRONOTE)){
                        personneDto.setIdPronote(externalId.getId());
                    }
                }
            }
        }
        setAllFonctions(personneDto, fonctionService.getPersonneFonctions(id));
        personneDto.setRelations(relationService.getPersonneRelations(id));
        return personneDto;
    }

    private void setAllFonctions(PersonneDto personneDto, List<FonctionDto> fonctions) {
        if (!fonctions.isEmpty()) {
            for(FonctionDto fonctionDto : fonctions){
                // Ici c'est acceptable de faire une boucle imbriquée car on a peu de structures dans la liste la pluspart du temps
                for(StructureForUserDto structureForUserDto : personneDto.getListeStructures()){
                    if(fonctionDto.getStructure().equals(structureForUserDto.getId())){
                        TypeFonctionFiliere typeFonctionFiliere = fonctionService.getTypeFonctionFiliere(fonctionDto.getFiliere());
                        Discipline discipline = fonctionService.getDiscipline(fonctionDto.getDiscipline());
                        FonctionDisplayDto fonctionDisplayDto = new FonctionDisplayDto(typeFonctionFiliere, discipline, fonctionDto.getDateFin());
                        if(!fonctionDto.getSource().startsWith(Constants.SARAPISUI_)){
                            structureForUserDto.getFonctions().add(fonctionDisplayDto);
                        } else {
                            structureForUserDto.getAdditionalFonctions().add(fonctionDisplayDto);
                        }
                    }
                }
            }
        }
    }

    public SimplePersonneDto getPersonneSimple(Long id) {
        return aPersonneRepository.findByPersonneIdSimple(id);
    }

}
