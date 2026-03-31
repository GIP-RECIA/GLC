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

import fr.recia.glc.audit.AuditEvent;
import fr.recia.glc.audit.AuditService;
import fr.recia.glc.audit.EventType;
import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.repositories.structure.AStructureRepository;
import fr.recia.glc.security.GLCRole;
import fr.recia.glc.security.GLCUser;
import fr.recia.glc.services.access.RightsService;
import fr.recia.glc.services.structure.StructureLoader;
import fr.recia.glc.web.dto.access.rights.AddOrDeleteMemberRequest;
import fr.recia.glc.web.dto.access.rights.ServiceAccess;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/api/rights")
public class RightsController {

    @Autowired
    private RightsService rightsService;
    @Autowired
    private AStructureRepository<AStructure> aStructureRepository;
    @Autowired
    private StructureLoader structureLoader;
    @Autowired
    private AuditService auditService;

    private String deductBranchFromStructure(AStructure aStructure) {
        log.debug("Retrieving branch for structure {}", aStructure.getId());
        final String branch = structureLoader.getBranchOfStructure(((Etablissement) aStructure).getUai());
        log.debug("Branch for structure {} is {}", aStructure.getId(), branch);
        return branch;
    }

    private String deductGroupNameFromStructure(AStructure aStructure) {
        log.debug("Retrieving group name for structure {}", aStructure.getId());
        final String etabGroupLeft = structureLoader.getGroupNameOfStructure(((Etablissement) aStructure).getUai());
        final String etabGroupRight = ((Etablissement) aStructure).getUai();
        String groupName = etabGroupLeft + "_" + etabGroupRight;
        log.debug("Group name for structure {} is {}", aStructure.getId(), groupName);
        return groupName;
    }

    @GetMapping("/{id}")
    public ResponseEntity<List<ServiceAccess>> listRights(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id,
                                                          @RequestParam(required = false, defaultValue = "false") boolean showExternal,
                                                          @RequestParam(required = false, defaultValue = "true") boolean showAdmin) {
        log.debug("Listing rights for structure {}", id);
        final AStructure aStructure = aStructureRepository.findById(id).orElseThrow();
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.READ);
        // TODO : gérer le cas des collectivités
        if (allowedSiren.contains(aStructure.getSiren())) {
            final String etabGroup = deductGroupNameFromStructure(aStructure);
            final String branch = deductBranchFromStructure(aStructure);
            final List<ServiceAccess> rights = rightsService.getRights(branch, etabGroup, showExternal, showAdmin);
            log.debug("Rights for structure {} are {}", id, rights);
            return ResponseEntity.ok(rights);
        } else {
            log.warn("User {} is not authorized to list rights in {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/{id}/services/{service}/roles/{role}/members")
    public ResponseEntity<Void> updateRights(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id,
                                             @PathVariable String service, @PathVariable String role,
                                             @RequestBody AddOrDeleteMemberRequest request) {
        log.debug("Updating rights for structure {}", id);
        final AStructure aStructure = aStructureRepository.findById(id).orElseThrow();
        Set<String> allowedSiren = principal.getRightsForEtabs().get(GLCRole.WRITE);
        // TODO : gérer le cas des collectivités
        if (allowedSiren.contains(aStructure.getSiren())) {
            final String etabGroup = deductGroupNameFromStructure(aStructure);
            final String branch = deductBranchFromStructure(aStructure);
            for (String memberToAdd : request.getMembersToAdd()) {
                log.debug("Adding right for member {} in structure {} for service {} for role {}", memberToAdd, id, service, role);
                rightsService.addRight(service, role, memberToAdd, branch, etabGroup);
            }
            for (String memberToRemove : request.getMembersToRemove()) {
                log.debug("Removing right for member {} in structure {} for service {} for role {}", memberToRemove, id, service, role);
                rightsService.removeRight(service, role, memberToRemove, branch, etabGroup);
            }
            // Log Audit
            auditService.log(
                AuditEvent.builder()
                    .timestamp(OffsetDateTime.now(ZoneId.systemDefault()))
                    .eventType(EventType.UPDATE_RIGHTS)
                    .actor(principal.getUsername())
                    .target(String.valueOf(id))
                    .payload(Map.of(
                        "service", service,
                        "role", role,
                        "request", request
                    ))
                    .build()
            );
            return ResponseEntity.ok().build();
        } else {
            log.warn("User {} is not authorized to update rights in {}", principal.getUsername(), id);
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }
}
