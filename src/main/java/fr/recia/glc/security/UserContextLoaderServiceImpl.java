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

import fr.recia.glc.configuration.GLCProperties;
import fr.recia.glc.db.repositories.structure.TypeStructureRepository;
import fr.recia.glc.ldap.IStructure;
import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.ldap.enums.PermissionType;
import fr.recia.glc.services.beans.IStructureLoader;
import fr.recia.glc.services.beans.UserContextRole;
import fr.recia.glc.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Complex bean to obtains objects Organization, Publisher, Category, Internal/ExternalFeed, Item where the authenticated user has a permission.
 * We should have permission type of PermissionOnContext only for Organization and Publisher. Publisher will give the type of permission for after.
 * Warning permission of type OnSubjects can be on publisher, but they won't be applied
 */
@Slf4j
@Service
public class UserContextLoaderServiceImpl implements UserContextLoaderService {
  private final TypeStructureRepository typeStructureRepository;

  @Inject
  public UserContextRole userSessionRoles;

  @Inject
  public IStructureLoader structureLoader;

  private GLCProperties glcProperties;

  public UserContextLoaderServiceImpl(GLCProperties glcProperties,
                                      TypeStructureRepository typeStructureRepository) {
    this.glcProperties = glcProperties;
    this.typeStructureRepository = typeStructureRepository;
  }

  public void loadUserRoles(Authentication authentication) {
    loadUserRoles(
      ((CustomUserDetails) authentication.getPrincipal()).getUser(),
      ((CustomUserDetails) authentication.getPrincipal()).getAuthorities()
    );
  }

  public void doExpireForReload() {
    userSessionRoles.setExpiringInstant(null);
  }

  public synchronized void loadUserRoles(final UserDTO user, final Collection<? extends GrantedAuthority> authorities) {
    // init userTree
    if (!userSessionRoles.loadingCanBeDone()) {
      log.debug("loadUserRoles can't be done !");
      return;
    }

    if (userSessionRoles.areRolesLoadInProgress()) {
      // we should wait that the loading is done
      long totalSleep = 0;
      boolean isInterrupted = false;
      log.debug("watching loading : {}", userSessionRoles.toString());
      while (userSessionRoles.areRolesLoadInProgress() && totalSleep < 10000) {
        try {
          log.debug("waiting loading : {}", userSessionRoles.toString());
          Thread.sleep(300);
          totalSleep += 300;
        } catch (InterruptedException e) {
          isInterrupted = true;
        }
      }
      if (totalSleep > 10000) isInterrupted = true;

      if (isInterrupted)
        throw new IllegalStateException("The tree loader was interrupted for the user " + user.toString());
      return;
    }

    log.warn("========================= WARNING loadingUserTree ========================");
    userSessionRoles.processingLoading();

    if (log.isDebugEnabled())
      log.debug(
        "\n<==\n\t- UID : {}\n\t- authorities : {}\n\t- isMemberOf : {}\n==>",
        user.getUserId(),
        authorities,
        user.getAttributes().get("isMemberOf").stream().map(s -> "\t\t- " + s).collect(Collectors.joining("\n", "\n", "\n"))
      );

    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) {
      userSessionRoles.setSuperAdmin(true);
    } else if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
      userSessionRoles.setSuperAdmin(false);
      log.debug("Call loadUserRoles for USER access !");

      List<String> allowed = new ArrayList<>();
      List<String> denied = new ArrayList<>();

      Pattern permissionPattern = Pattern.compile(glcProperties.getUsers().getGroupName());
      user.getAttributes().get("isMemberOf").forEach(item -> {
        Matcher matcher = permissionPattern.matcher(item);
        if (matcher.find()) {
          PermissionType permission = null;
          switch (matcher.group(2)) {
            case "admin":
              permission = PermissionType.ADMIN;
              break;
            case "local":
              permission = PermissionType.MANAGER;
              break;
            case "ESCOLAN":
              permission = PermissionType.MANAGER;
              break;
            default:
              break;
          }
          if (matcher.groupCount() > 2) {
            StructureKey structureKey = structureLoader.getAllStructures().stream()
              .filter(structure -> structure.getUAI().equals(matcher.group(3)))
              .map(IStructure::getStructureKey)
              .findAny().orElse(null);
            if (structureKey != null) {
              allowed.add(item);
              userSessionRoles.addCtx(structureKey, permission);
            } else denied.add(item);
          }
        }
      });

      if (log.isDebugEnabled())
        log.debug(
          "\n<==\n\t- allowed : {}\n\t- denied : {}\n==>",
          allowed.stream().map(s -> "\t\t- " + s).collect(Collectors.joining("\n", "\n", "\n")),
          denied.stream().map(s -> "\t\t- " + s).collect(Collectors.joining("\n", "\n", "\n"))
        );
    } else userSessionRoles.setSuperAdmin(false);
    userSessionRoles.notifyEndLoading();

    if (log.isDebugEnabled()) log.debug("Tree loaded : {}", userSessionRoles.toString());
  }

}
