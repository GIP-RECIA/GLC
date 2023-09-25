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
import com.google.common.collect.Sets;
import com.mysema.commons.lang.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Complex bean to obtains objects Organization, Publisher, Category, Internal/ExternalFeed, Item where the authenticated user has a permission.
 * We should have permission type of PermissionOnContext only for Organization and Publisher. Publisher will give the type of permission for after.
 * Warning permission of type OnSubjects can be on publisher, but they won't be applied
 */
@Slf4j
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class UserContextLoaderServiceImpl implements UserContextLoaderService {


	@Inject
	public UserContextTree userSessionTree;

	public void loadUserTree(Authentication authentication) {
		loadUserTree(((CustomUserDetails) authentication.getPrincipal()).getUser(),
				((CustomUserDetails) authentication.getPrincipal()).getAuthorities());
	}

	public void doExpireForReload() {
	    userSessionTree.setExpiringInstant(null);
    }

	public synchronized void loadUserTree(final UserDTO user, final Collection<? extends GrantedAuthority> authorities) {
		// init userTree
        if (!userSessionTree.loadingCanBeDone()) {
            log.debug("loadUserTree can't be done !");
            return;
        }
        if (userSessionTree.isTreeLoadInProgress()) {
            // we should wait that the loading is done
            long totalSleep = 0;
            boolean isInterrupted = false;
            log.debug("watching loading : {}", userSessionTree.toString());
            while (userSessionTree.isTreeLoadInProgress() && totalSleep < 10000) {
                try {
                    log.debug("waiting loading : {}", userSessionTree.toString());
                    Thread.sleep(300);
                    totalSleep += 300;
                } catch (InterruptedException e) {
                    isInterrupted = true;
                }
            }
            if (totalSleep > 10000) {
                isInterrupted = true;
            }

            if (isInterrupted) {
                throw new IllegalStateException("The tree loader was interrupted for the user " + user.toString());
            }
            return;
        }
        log.warn("========================= WARNING loadingUserTree ========================");
		userSessionTree.processingLoading();
		if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ADMIN))) {
			userSessionTree.setSuperAdmin(true);
		} else if (authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.USER))) {
			userSessionTree.setSuperAdmin(false);
			log.debug("Call loadUserTree for USER access !");
			// Load list of organizations

			@SuppressWarnings("unchecked")
			final List<PermissionOnContext> perms = Lists.newArrayList(permissionDao.getPermissionDao(
					PermissionClass.CONTEXT).findAll(
					PermissionPredicates.OnCtxType(ContextType.ORGANIZATION, PermissionClass.CONTEXT, false)));
			Map<ContextKey, PermissionOnContext> ctxRoles = Maps.newHashMap();
			// Evaluate perms on all Organizations, all users should have a role on the organization to access on it
			for (PermissionOnContext perm : perms) {
				if (evaluationFactory.from(perm.getEvaluator()).isApplicable(user)
						&& perm.getRole().getMask() >= PermissionType.LOOKOVER.getMask()) {
					if (log.isDebugEnabled()) {
						log.debug("TreeLoader should add {}", perm.getContext());
					}
					if (ctxRoles.containsKey(perm.getContext())) {
						PermissionOnContext role = ctxRoles.get(perm.getContext());
						if (role == null || perm.getRole().getMask() > role.getRole().getMask()) {
							ctxRoles.put(perm.getContext(), perm);
						}
					} else {
						ctxRoles.put(perm.getContext(), perm);
					}
				}
			}

			// now we can go on childs
			for (Map.Entry<ContextKey, PermissionOnContext> ctx : ctxRoles.entrySet()) {
				if (ctx.getValue() != null && PermissionType.MANAGER.getMask() <= ctx.getValue().getRole().getMask()) {
					final PermOnCtxDTO permDTO = (PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue());
					userSessionTree.addCtx(ctx.getKey(), false, null, null, permDTO);
					loadAuthorizedOrganizationChilds(user, ctx.getKey(), false, new Pair<PermissionType, PermOnCtxDTO>(
							ctx.getValue().getRole(), permDTO));
				} else {
					final PermOnCtxDTO permDTO = (PermOnCtxDTO) permissionDTOFactory.from(ctx.getValue());
					loadAuthorizedOrganizationChilds(user, ctx.getKey(), true, new Pair<PermissionType, PermOnCtxDTO>(
							ctx.getValue().getRole(), permDTO));
				}
			}
		} else {
			userSessionTree.setSuperAdmin(false);
		}
		userSessionTree.notifyEndLoading();
		if (log.isDebugEnabled()) {
			log.debug("Tree loaded : {}", userSessionTree.toString());
		}
	}

}
