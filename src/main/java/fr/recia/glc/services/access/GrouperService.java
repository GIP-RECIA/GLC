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

import fr.recia.glc.configuration.Constants;
import fr.recia.glc.configuration.bean.GrouperProperties;
import fr.recia.glc.web.dto.access.grouper.request.add.WsRestAddMemberRequestWrapper;
import fr.recia.glc.web.dto.access.grouper.request.delete.WsRestDeleteMemberRequestWrapper;
import fr.recia.glc.web.dto.access.grouper.request.find.WsRestFindGroupsRequestWrapper;
import fr.recia.glc.web.dto.access.grouper.response.add.WsAddMemberResponse;
import fr.recia.glc.web.dto.access.grouper.response.find.WsFindGroupsResponse;
import fr.recia.glc.web.dto.access.grouper.response.remove.WsDeleteMemberResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Service
public class GrouperService {

    private final RestTemplate restTemplate;
    private final GrouperProperties grouperProperties;
    private final LdapTemplate ldapTemplate;

    public GrouperService(RestTemplate restTemplate, GrouperProperties grouperProperties, LdapTemplate ldapTemplate) {
        this.restTemplate = restTemplate;
        this.grouperProperties = grouperProperties;
        this.ldapTemplate = ldapTemplate;
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf(Constants.GROUPER_MEDIA_TYPE));
        headers.setAccept(Collections.singletonList(MediaType.valueOf(Constants.GROUPER_MEDIA_TYPE)));
        headers.setBasicAuth(grouperProperties.getUsername(), grouperProperties.getPassword());
        return headers;
    }

    /**
     * Récupération de l'id d'un groupe par son chemin ID
     */
    private String findGroupId(String groupName) {
        String url = grouperProperties.getBaseUrl() + "/groups";
        WsRestFindGroupsRequestWrapper payload = new WsRestFindGroupsRequestWrapper(groupName);
        HttpEntity<?> entity = new HttpEntity<>(payload, createHeaders());
        ResponseEntity<WsFindGroupsResponse> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, WsFindGroupsResponse.class);
        if (responseEntity.getBody() != null) {
            return responseEntity.getBody().getWsFindGroupsResults().getGroupResults().get(0).getUuid();
        }
        return null;
    }

    /**
    * Lister les membres d'un groupe via une requête LDAP
    * @return La liste des membres ou une liste vide s'il n'y a pas de membres
    */
    public List<String> listGroupMembers(String groupName){
        return ldapTemplate.search(query().base("ou=groups").where("cn").is(groupName),
                (AttributesMapper<List<String>>) attrs -> {
                    List<String> members = new ArrayList<>();
                    Attribute memberAttr = attrs.get("member");
                    if (memberAttr != null) {
                        NamingEnumeration<?> values = memberAttr.getAll();
                        // Récupérer la partie intéréssante dans le cn
                        while (values.hasMore()) {
                            String val = values.next().toString();
                            members.add(val.substring(val.indexOf('=')+1, val.indexOf(',')));
                        }
                    }
                    return members;
                }
        ).stream().findFirst().orElse(List.of());
    }

    /**
     * Ajouter un membre dans un groupe
     * @param groupName Le nom du groupe dans lequel on veut ajouter le membre
     * @param subjectId Soit l'uid de la personne à ajouter, soit le chemin ID du groupe à ajouter
     * @param isGroup Un booléen qui indique si c'est un groupe ou une personne qui est à ajouter
     * @return
     */
    public ResponseEntity<WsAddMemberResponse> addMember(String groupName, String subjectId, boolean isGroup) {
        String url = grouperProperties.getBaseUrl() + "/groups/" + groupName + "/members";
        HttpHeaders headers = createHeaders();
        String sourceId = Constants.GROUPER_SOURCEID_USER;
        if(isGroup){
            sourceId = Constants.GROUPER_SOURCEID_GROUP;
            subjectId = findGroupId(subjectId);
        }
        WsRestAddMemberRequestWrapper payload = new WsRestAddMemberRequestWrapper(subjectId, sourceId);
        HttpEntity<?> entity = new HttpEntity<>(payload, headers);
        return restTemplate.exchange(url, HttpMethod.PUT, entity, WsAddMemberResponse.class);
    }


    /**
     * Supprimer un membre d'un groupe
     * @param groupName Le nom du groupe duquel on veut supprimer le membre
     * @param subjectId Soit l'uid de la personne à ajouter, soit le chemin ID du groupe à supprimer
     * @param isGroup Un booléen qui indique si c'est un groupe ou une personne qui est à supprimer
     * @return
     */
    public ResponseEntity<WsDeleteMemberResponse> removeMember(String groupName, String subjectId, boolean isGroup) {
        String url = grouperProperties.getBaseUrl() + "/groups/" + groupName + "/members";
        HttpHeaders headers = createHeaders();
        String sourceId = Constants.GROUPER_SOURCEID_USER;
        if(isGroup){
            sourceId = Constants.GROUPER_SOURCEID_GROUP;
            subjectId = findGroupId(subjectId);
        }
        WsRestDeleteMemberRequestWrapper payload = new WsRestDeleteMemberRequestWrapper(subjectId, sourceId);
        HttpEntity<?> entity = new HttpEntity<>(payload, headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, WsDeleteMemberResponse.class);
    }

}
