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
package fr.recia.glc.services.access;

import com.querydsl.core.types.Predicate;
import fr.recia.glc.configuration.Constants;
import fr.recia.glc.configuration.bean.RightsProperties;
import fr.recia.glc.configuration.bean.RoleProperties;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.personne.QAPersonne;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.services.exceptions.UnauthorizedGroupModificationException;
import fr.recia.glc.services.exceptions.UnauthorizedPeopleModificationException;
import fr.recia.glc.services.helpers.GroupPathGenerator;
import fr.recia.glc.web.dto.access.grouper.response.WsMembership;
import fr.recia.glc.web.dto.access.grouper.response.WsSubject;
import fr.recia.glc.web.dto.access.grouper.response.add.WsAddMemberResponse;
import fr.recia.glc.web.dto.access.grouper.response.memberships.WsGetMembershipsResponse;
import fr.recia.glc.web.dto.access.grouper.response.remove.WsDeleteMemberResponse;
import fr.recia.glc.web.dto.access.rights.Member;
import fr.recia.glc.web.dto.access.rights.Right;
import fr.recia.glc.web.dto.access.rights.ServiceAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class RightsService {

    private final RightsProperties rightsProperties;
    private final GrouperService grouperService;
    private final Map<String, String> invertedTemplateCache;
    private final APersonneRepository<APersonne> aPersonneRepository;

    public RightsService(RightsProperties rightsProperties, GrouperService grouperService, APersonneRepository<APersonne> aPersonneRepository){
        this.rightsProperties = rightsProperties;
        this.grouperService = grouperService;
        this.aPersonneRepository = aPersonneRepository;
        this.invertedTemplateCache = new HashMap<>();
    }

    private List<Member> getGroupsFullPathAndCache(List<String> memberList, String branch, String etabName){
        List<Member> groups = new ArrayList<>();
        for(String canonicalName : memberList){
            String resolvedName = GroupPathGenerator.groupPathFromTemplate(canonicalName, branch, etabName);
            groups.add(new Member(resolvedName, rightsProperties.getDeclaredGroupsMap().get(canonicalName), false, false));
            // Add to inverted cache
            invertedTemplateCache.put(resolvedName, canonicalName);
        }
        return groups;
    }

    private boolean isGroup(String memberName){
        return !(memberName.startsWith("F") && memberName.length()==8);
    }

    /**
     * Donne la liste de tous les droits des services pour le front d'après la configuration
     */
    public List<ServiceAccess> getRights(String branch, String etabGroup, boolean showExternal, boolean showAdmin){
        List<ServiceAccess> serviceAccessList = new ArrayList<>();
        for(String service: rightsProperties.getServices().keySet()){
            ServiceAccess serviceAccess = new ServiceAccess();
            serviceAccess.setService(service);
            List<Right> rights = new ArrayList<>();
            final Map<String, RoleProperties> rolesByService = rightsProperties.getServices().get(service);
            for(String role : rolesByService.keySet()){
                final RoleProperties roleProperties = rolesByService.get(role);
                Right right = new Right();
                right.setDescription(roleProperties.getDescription());
                right.setRole(role);
                // Ajout des groupes par configuration
                List<Member> possibleGroups = new ArrayList<>(getGroupsFullPathAndCache(roleProperties.getPossibleGroups(), branch, etabGroup));
                right.setPossibleGroups(possibleGroups);
                List<Member> mandatoryGroups = new ArrayList<>(getGroupsFullPathAndCache(roleProperties.getMandatoryGroups(), branch, etabGroup));
                right.setMandatoryGroups(mandatoryGroups);
                // Ajout des membres déjà présents via grouper
                List<Member> currentMembers = new ArrayList<>();
                WsGetMembershipsResponse wsGetMembershipsResponse = grouperService.listMemberships(GroupPathGenerator.groupPathFromTemplate(
                        roleProperties.getTargetGroup(), branch, etabGroup), showExternal, true, true).getBody();
                List<WsSubject> wsSubjectList = wsGetMembershipsResponse.getResults().getWsSubjects();
                log.info("Subjets retrieved for group {} are : {}", roleProperties.getTargetGroup(), wsSubjectList);
                for(WsSubject wsSubject : wsSubjectList){
                    boolean isUser = !wsSubject.getSourceId().equals(Constants.GROUPER_SOURCEID_GROUP);
                    Member member;
                    if(isUser){
                        // TODO : faire une requête pour toutes les personnes d'un coup pour des raisons de performance
                        Predicate predicate = QAPersonne.aPersonne.uid.eq(wsSubject.getId());
                        member = new Member(wsSubject.getId(), aPersonneRepository.findOne(predicate).get().getDisplayName(), true, true);
                    } else {
                        if(invertedTemplateCache.containsKey(wsSubject.getName())){
                            member = new Member(wsSubject.getName(), rightsProperties.getDeclaredGroupsMap().get(invertedTemplateCache.get(wsSubject.getName())),false, false);
                        } else {
                            member = new Member(wsSubject.getName(), wsSubject.getName(),false, true);
                        }
                    }
                    currentMembers.add(member);

                }
                right.setCurrentMembers(currentMembers);
                right.setAllowPeople(rolesByService.get(role).isAllowPeople());
                right.setAdmin(rolesByService.get(role).isAdmin());
                if(right.isAdmin() && !showAdmin){

                } else {
                    rights.add(right);
                }
            }
            serviceAccess.setRights(rights);
            serviceAccessList.add(serviceAccess);
        }
        return serviceAccessList;
    }

    /**
     * Ajoute un droit sur un service pour un membre donné
     */
    public ResponseEntity<WsAddMemberResponse> addRight(String service, String role, String memberToAdd, String branch, String etabGroup){
        // Récupération du targetGroup depuis la conf pour être sûr qu'on ajoute sur le bon
        String targetGroup = GroupPathGenerator.groupPathFromTemplate(rightsProperties.getServices().get(service).get(role).getTargetGroup(), branch, etabGroup);
        // Vérification qu'on a bien le droit d'ajouter le membre en question
        // Le défaut de ce sytème de vérification c'est qu'il faut les avoir chargé dans le cache d'abord (donc avec un GET) mais normalement
        // on est pas censé faire des POST directement si on passe par l'UI
        if(isGroup(memberToAdd)){
            if(!rightsProperties.getServices().get(service).get(role).getPossibleGroups().contains(invertedTemplateCache.get(memberToAdd))
                    && !rightsProperties.getServices().get(service).get(role).getMandatoryGroups().contains(invertedTemplateCache.get(memberToAdd))){
                throw new UnauthorizedGroupModificationException("Can't add member "+memberToAdd+" for role "+role+" in service "+service);
            }

        } else {
            if(!rightsProperties.getServices().get(service).get(role).isAllowPeople()){
                throw new UnauthorizedPeopleModificationException("Can't add member "+memberToAdd+" for role"+role+" in service "+service);
            }
        }
        return grouperService.addMember(targetGroup, memberToAdd, isGroup(memberToAdd));
    }

    /**
     * Retire un droit sur un service pour un membre donné
     */
    public ResponseEntity<WsDeleteMemberResponse> removeRight(String service, String role, String memberToRemove, String branch, String etabGroup){
        // Récupération du targetGroup depuis la conf pour être sûr qu'on ajoute sur le bon
        String targetGroup = GroupPathGenerator.groupPathFromTemplate(rightsProperties.getServices().get(service).get(role).getTargetGroup(), branch, etabGroup);
        // Vérification qu'on a bien le droit de supprimer le membre en question
        if(isGroup(memberToRemove)){
            if(!rightsProperties.getServices().get(service).get(role).getPossibleGroups().contains(invertedTemplateCache.get(memberToRemove))){
                throw new UnauthorizedGroupModificationException("Can't remove member "+memberToRemove+" for role"+role+" in service "+service);
            }
        } else {
            if(!rightsProperties.getServices().get(service).get(role).isAllowPeople()){
                throw new UnauthorizedPeopleModificationException("Can't remove member "+memberToRemove+" for role"+role+" in service "+service);
            }
        }
        return grouperService.removeMember(targetGroup, memberToRemove, isGroup(memberToRemove));
    }
}
