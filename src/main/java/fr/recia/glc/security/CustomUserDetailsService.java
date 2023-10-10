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

import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.entities.personne.QAPersonne;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import fr.recia.glc.ldap.IExternalUser;
import fr.recia.glc.ldap.repository.IExternalUserDao;
import fr.recia.glc.services.factories.UserDTOFactory;
import fr.recia.glc.web.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Collection;

/**
 * Authenticate a user from the database.
 */
@Service
@Slf4j
public class CustomUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

  @Inject
  private transient APersonneRepository<APersonne> aPersonneRepository;

  @Inject
  private transient IExternalUserDao personLDAPDao;

  @Inject
  private transient IAuthorityService grantedAuthorityService;

  @Inject
  private transient UserDTOFactory userDTOFactory;

  public CustomUserDetailsService() {
    super();
  }

  @Override
  @Transactional
  public CustomUserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
    String uid = token.getPrincipal().toString();
    log.debug("Authenticating '{}'", uid);

    return loadUserByUid(uid);
  }

  @Transactional
  public CustomUserDetails loadUserByUid(String uid) throws UsernameNotFoundException {
    final APersonne internal = aPersonneRepository.findOne(QAPersonne.aPersonne.uid.eq(uid)).orElse(null);
    final IExternalUser external = personLDAPDao.getUserByUid(uid);
    if (external == null)
      throw new UsernameNotFoundException(String.format("User [%s] without permission !", uid));
    UserDTO user = userDTOFactory.from(internal, external);
    Collection<? extends GrantedAuthority> authorities = grantedAuthorityService.getUserAuthorities(user);

    if (internal == null) {
      log.error("User with username {} could not found from database.", uid);
      throw new UsernameNotFoundException(String.format("User with username [%s] could not be found from database.", uid));
    }

    return new CustomUserDetails(userDTOFactory.from(internal, external), internal, authorities);
  }

}
