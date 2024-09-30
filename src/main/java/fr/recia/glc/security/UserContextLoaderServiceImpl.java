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
import fr.recia.glc.ldap.enums.PermissionType;
import fr.recia.glc.services.beans.IStructureLoader;
import fr.recia.glc.services.beans.UserContextRole;
import fr.recia.glc.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Collection;
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

  @Autowired
  public UserContextRole userSessionRoles;

  @Autowired
  public IStructureLoader structureLoader;

  private final GLCProperties glcProperties;

  public UserContextLoaderServiceImpl(GLCProperties glcProperties, TypeStructureRepository typeStructureRepository) {
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

    userSessionRoles.setSuperAdmin(false);
    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) {
      userSessionRoles.setSuperAdmin(true);
    } else if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
      log.debug("Call loadUserRoles for USER access !");

      Pattern permissionPattern = Pattern.compile(glcProperties.getUsers().getGroupName());
      user.getAttributes().get("isMemberOf").forEach(item -> {
        Matcher matcher = permissionPattern.matcher(item);
        if (matcher.find()) {
          log.debug("Pattern match rights on structure for group attribute '{}', count of matchers '{}'", item, matcher.groupCount());
          PermissionType permission = null;
          switch (matcher.group(2)) {
            case "central":
              permission = PermissionType.MANAGER_BRANCH;
              break;
            case "local":
              permission = PermissionType.MANAGER;
              break;
            case "ESCOLAN":
              permission = PermissionType.MANAGER_BRANCH;
              break;
            default:
              break;
          }
          if (PermissionType.MANAGER.equals(permission) && matcher.groupCount() > 2) {
            log.debug("MANAGER + matcher group 3  {}", matcher.group(3));
            structureLoader.getAllStructures().stream()
              .filter(structure -> structure.getUAI().equals(matcher.group(3)))
              .findFirst()
              .ifPresent(structure -> {
                log.debug("Add structure key {} to managed branch", structure.getStructureKey());
                userSessionRoles.addCtx(structure.getStructureKey(), PermissionType.MANAGER);
              });
          } else if (PermissionType.MANAGER_BRANCH.equals(permission)) {
            log.debug("MANAGER_BRANCH + matcher group 1  {}", matcher.group(1));
            // TODO nécessité de fournir l'ensemble des structures ?
            structureLoader.getAllStructures().stream()
              .filter(structure -> structure.getGroupBranch().equals(matcher.group(1)))
              .forEach(structure -> {
                log.debug("Add structure key {} to managed branch", structure.getStructureKey());
                userSessionRoles.addCtx(structure.getStructureKey(), PermissionType.MANAGER_BRANCH);
              });
          }
        }
      });
    }
    userSessionRoles.notifyEndLoading();

    if (log.isDebugEnabled()) log.debug("Tree loaded : {}", userSessionRoles.toString());
  }

}
