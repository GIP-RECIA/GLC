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
package fr.recia.glc.web.rest;

import fr.recia.glc.services.access.RightsService;
import fr.recia.glc.web.dto.access.grouper.response.add.WsAddMemberResponse;
import fr.recia.glc.web.dto.access.grouper.response.remove.WsDeleteMemberResponse;
import fr.recia.glc.web.dto.access.rights.AddOrDeleteMemberRequest;
import fr.recia.glc.web.dto.access.rights.ServiceAccess;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rights")
public class RightsController {

    private final RightsService rightsService;

    public RightsController(RightsService rightsService) {
        this.rightsService = rightsService;
    }

    @GetMapping("/{uai}")
    public ResponseEntity<List<ServiceAccess>> listRights(@PathVariable String uai) {
        // TODO : on récupère la branch et le nom de l'établissement utilisé pour les groupes grouper via un service qui requête le LDAP
        final String branch = "clg45";
        final String etabGroup = "FICTIF CLG 45_0450000A";
        return ResponseEntity.ok(rightsService.getRights(branch, etabGroup));
    }

    @PutMapping("/{uai}/services/{service}/roles/{role}/members")
    public ResponseEntity<Void> updateRights(@PathVariable String uai,
                                             @PathVariable String service,
                                             @PathVariable String role,
                                             @RequestBody AddOrDeleteMemberRequest request){
        // TODO : on récupère la branch et le nom de l'établissement utilisé pour les groupes grouper via un service qui requête le LDAP
        final String branch = "clg45";
        final String etabGroup = "FICTIF CLG 45_0450000A";
        for(String memberToAdd : request.getMembersToAdd()){
            rightsService.addRight(service, role, memberToAdd, branch, etabGroup);
        }
        for(String memberToRemove : request.getMembersToRemove()){
            rightsService.removeRight(service, role, memberToRemove, branch, etabGroup);
        }
        return ResponseEntity.ok().build();
    }

}
