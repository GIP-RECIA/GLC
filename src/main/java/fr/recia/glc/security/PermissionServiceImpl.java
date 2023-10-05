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

import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.ldap.enums.PermissionType;
import fr.recia.glc.services.beans.UserContextRole;
import fr.recia.glc.services.factories.UserDTOFactory;
import fr.recia.glc.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Service(value = "permissionService")
@Lazy
@Scope(proxyMode = ScopedProxyMode.INTERFACES)
@Slf4j
public class PermissionServiceImpl implements IPermissionService {

  @Inject
  public UserDTOFactory userDTOFactory;

  @Inject
  public UserContextRole userContextRole;
  @Inject
  public UserContextLoaderService userContextLoaderService;

  public PermissionServiceImpl() {
    super();
  }

  @Override
  public PermissionType getRoleOfUserInContext(
    @NotNull Authentication authentication, @NotNull final StructureKey structureKey
  ) {
    return getRoleOfUserInContext(
      ((CustomUserDetails) authentication.getPrincipal()).getUser(),
      ((CustomUserDetails) authentication.getPrincipal()).getAuthorities(),
      structureKey);
  }

  private PermissionType getRoleOfUserInContext(
    final UserDTO user, final Collection<? extends GrantedAuthority> authorities, final StructureKey structureKey
  ) {
    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) return PermissionType.ADMIN;

    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER)) && structureKey != null) {
      if (!userContextRole.areRolesLoaded()) userContextLoaderService.loadUserRoles(user, authorities);

      return userContextRole.getRoleFromContext(structureKey);
    }

    return null;
  }

  @Override
  public boolean canManageCtx(@NotNull Authentication authentication, @NotNull StructureKey structureKey) {
    final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
    final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

    log.debug("Testing canManageCtx in context {} for  user {}", structureKey, user);
    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) return true;

    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
      if (!userContextRole.areRolesLoaded()) userContextLoaderService.loadUserRoles(user, authorities);
      final PermissionType perm = userContextRole.getRoleFromContext(structureKey);

      return perm != null && perm.getMask() >= PermissionType.MANAGER.getMask();
    }

    return false;
  }

  public boolean canViewCtx(@NotNull Authentication authentication, @NotNull StructureKey structureKey) {
    final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
    final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();
    log.debug("Testing canViewCtx in context {} for  user {}", structureKey, user);
    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) return true;

    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
      if (!userContextRole.areRolesLoaded()) userContextLoaderService.loadUserRoles(user, authorities);
      final PermissionType perm = userContextRole.getRoleFromContext(structureKey);

      return perm != null && perm.getMask() >= PermissionType.LOOKOVER.getMask();
    }

    return false;
  }

  @Override
  public boolean hasAuthorizedStructure(@NotNull Authentication authentication) {
    final UserDTO user = ((CustomUserDetails) authentication.getPrincipal()).getUser();
    final Collection<? extends GrantedAuthority> authorities = ((CustomUserDetails) authentication.getPrincipal()).getAuthorities();

    log.debug("Testing hasAuthorizedStructure for user {}", user);

    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) return true;

    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
      if (!userContextRole.areRolesLoaded()) userContextLoaderService.loadUserRoles(user, authorities);

      return userContextRole.hasContextRoles();
    }

    return false;
  }

}
