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
package fr.recia.glc.security.admingroup;

import com.mysema.commons.lang.Pair;
import fr.recia.glc.ldap.IStructure;
import fr.recia.glc.security.cas.AuthoritiesConstants;
import fr.recia.glc.security.cas.CustomUserDetails;
import fr.recia.glc.security.cas.IPermissionService;
import fr.recia.glc.services.factories.UserDTOFactory;
import fr.recia.glc.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.esupportail.publisher.domain.ContextKey;
import org.esupportail.publisher.domain.QAbstractClassification;
import org.esupportail.publisher.domain.QAbstractFeed;
import org.esupportail.publisher.domain.QAbstractItem;
import org.esupportail.publisher.domain.QCategory;
import org.esupportail.publisher.domain.QItemClassificationOrder;
import org.esupportail.publisher.domain.QOrganization;
import org.esupportail.publisher.domain.QPublisher;
import org.esupportail.publisher.domain.enums.ContextType;
import org.esupportail.publisher.domain.enums.PermissionType;
import org.esupportail.publisher.repository.predicates.ClassificationPredicates;
import org.esupportail.publisher.service.ContextService;
import org.esupportail.publisher.service.bean.UserContextTree;
import org.esupportail.publisher.service.factories.UserDTOFactory;
import org.esupportail.publisher.web.rest.dto.PermissionDTO;
import org.esupportail.publisher.web.rest.dto.UserDTO;
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
//    @Inject
//    public UserContextTree userSessionTree;
    @Inject
    public UserContextLoaderService userSessionTreeLoader;
//
//    @Inject
//    public ContextService contextService;

    public PermissionServiceImpl() {
        super();
    }

    @Override
    public DroitApplicatif getRoleOfUserInContext(@NotNull Authentication authentication,
                                                 @NotNull final IStructure contextKey) {
        return getRoleOfUserInContext(
            ((CustomUserDetails) authentication.getPrincipal()).getUser(),
            ((CustomUserDetails) authentication.getPrincipal()).getAuthorities(),
            contextKey);
    }

    @Override
    public Pair<PermissionType, PermissionDTO> getPermsOfUserInContext(Authentication authentication, ContextKey contextKey) {
        if (contextKey.getKeyId() == null || contextKey.getKeyType() == null) return null;
        if (!userSessionTree.isTreeLoaded()) {
            userSessionTreeLoader.loadUserTree(((CustomUserDetails) authentication.getPrincipal()).getUser(),
                ((CustomUserDetails) authentication.getPrincipal()).getAuthorities());
        }
        return userSessionTree.getPermsFromContextTree(contextKey);
    }

    private DroitApplicatif getRoleOfUserInContext(final UserDTO user,
                                                  final Collection<? extends GrantedAuthority> authorities,
                                                  final IStructure contextKey) {
        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.ADMIN))) {
            return DroitApplicatif.SuperGlobalWriter;
        }

        if (authorities.contains(new SimpleGrantedAuthority(
            AuthoritiesConstants.USER)) && contextKey != null && contextKey.getStructureKey().getKeyId() != null && contextKey.getStructureKey().getKeyType() != null) {
            if (!userSessionTree.isTreeLoaded()) {
                userSessionTreeLoader.loadUserTree(user, authorities);
            }
            return userSessionTree.getRoleFromContextTree(contextKey);
        }

        return null;
    }

}
