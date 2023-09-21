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
package fr.recia.glc.security;

import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.ldap.IStructure;
import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.ldap.enums.PermissionType;
import fr.recia.glc.security.CustomUserDetails;
import fr.recia.glc.security.IPermissionService;
import fr.recia.glc.security.admingroup.DroitApplicatif;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import javax.inject.Inject;
import java.io.Serializable;

@Slf4j
public class CustomPermissionEvaluator implements PermissionEvaluator {

  @Inject
  public IPermissionService permissionService;

  public CustomPermissionEvaluator() {
    super();
  }

  @Override
  public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {

    if (!checkAuthentication(authentication)) {
      log.error("Authentication has something wrong, check that the user is authenticated and security is wheel implemented !");
      return false;
    }
    if (targetDomainObject == null || !(targetDomainObject instanceof IStructure)) {
      log.error("The object {} passed to check permission doesn't implements IStructure interface.", targetDomainObject);
      throw new IllegalArgumentException("The object passed to check permission doesn't implements IStructure interface.");
    }
    log.debug("Call custom hasPermission with object {} and permission {} !", ((IStructure) targetDomainObject).getStructureKey(), permission);
    IStructure context = (IStructure) targetDomainObject;

    PermissionType RoleInCtx = permissionService.getRoleOfUserInContext(authentication, context.getStructureKey());

    if (RoleInCtx == null) return false;

    PermissionType perm = PermissionType.valueOf((String) permission);

    return perm != null && RoleInCtx.getMask() >= perm.getMask();
  }

  @Override
  public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
    if (!checkAuthentication(authentication)) {
      log.error("Authentication has something wrong, check that the user is authenticated and security is wheel implemented !");
      return false;
    }
    log.debug(
      "Call custom hasPermission with id {}, type {} and permission {} !", targetId, targetType, permission);
    CategorieStructure ctxType = CategorieStructure.valueOf(targetType);

    PermissionType RoleInCtx = permissionService.getRoleOfUserInContext(authentication, new StructureKey((String) targetId, ctxType));

    if (RoleInCtx == null) return false;

    PermissionType perm = PermissionType.valueOf((String) permission);

    return perm != null && RoleInCtx.getMask() >= perm.getMask();
  }

  private boolean checkAuthentication(Authentication authentication) {
    return authentication != null && authentication.getPrincipal() != null && authentication.getPrincipal() instanceof CustomUserDetails;
  }

}
