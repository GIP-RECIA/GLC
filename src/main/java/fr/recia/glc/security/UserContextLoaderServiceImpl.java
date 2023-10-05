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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mysema.commons.lang.Pair;
import fr.recia.glc.ldap.StructureKey;
import fr.recia.glc.ldap.enums.ContextType;
import fr.recia.glc.ldap.enums.PermissionType;
import fr.recia.glc.services.beans.UserContextRole;
import fr.recia.glc.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Map;

/**
 * Complex bean to obtains objects Organization, Publisher, Category, Internal/ExternalFeed, Item where the authenticated user has a permission.
 * We should have permission type of PermissionOnContext only for Organization and Publisher. Publisher will give the type of permission for after.
 * Warning permission of type OnSubjects can be on publisher, but they won't be applied
 */
@Slf4j
@Service
public class UserContextLoaderServiceImpl implements UserContextLoaderService {

  @Inject
  public UserContextRole userSessionRoles;

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
    if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN)))
      userSessionRoles.setSuperAdmin(true);
    else if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
      userSessionRoles.setSuperAdmin(false);
      log.debug("Call loadUserRoles for USER access !");
      // Load list of organizations

//      @SuppressWarnings("unchecked")
//      final List<PermissionOnContext> perms = Lists.newArrayList(permissionDao
//        .getPermissionDao(PermissionClass.CONTEXT)
//        .findAll(PermissionPredicates.OnCtxType(ContextType.ORGANIZATION, PermissionClass.CONTEXT, false))
//      );
//      Map<StructureKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
//      // Evaluate perms on all Organizations, all users should have a role on the organization to access on it
//      for (PermissionOnContext perm : perms) {
//        if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
//          && perm.getRole().getMask() >= PermissionType.LOOKOVER.getMask()) {
//          if (log.isDebugEnabled()) log.debug("TreeLoader should add {}", perm.getContext());
//
//          if (ctxRoles.containsKey(perm.getContext())) {
//            PermissionOnContext role = ctxRoles.get(perm.getContext());
//            if (role == null || perm.getRole().getMask() > role.getRole().getMask())
//              ctxRoles.put(perm.getContext(), perm);
//          } else ctxRoles.put(perm.getContext(), perm);
//        }
//      }
//
//      // now we can go on childs
//      for (Map.Entry<StructureKey, PermissionOnContext> ctx : ctxRoles.entrySet()) {
//        if (ctx.getValue() != null && PermissionType.MANAGER.getMask() <= ctx.getValue().getRole().getMask()) {
//          final PermOnCtxDTO permDTO = (PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue());
//          userSessionTree.addCtx(ctx.getKey(), false, null, null, permDTO);
//          loadAuthorizedOrganizationChilds(user, ctx.getKey(), false, new Pair<>(ctx.getValue().getRole(), permDTO));
//        } else {
//          final PermOnCtxDTO permDTO = (PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue());
//          loadAuthorizedOrganizationChilds(user, ctx.getKey(), true, new Pair<>(ctx.getValue().getRole(), permDTO));
//        }
//      }
    } else userSessionRoles.setSuperAdmin(false);

    userSessionRoles.notifyEndLoading();

    if (log.isDebugEnabled()) log.debug("Tree loaded : {}", userSessionRoles.toString());
  }

}
