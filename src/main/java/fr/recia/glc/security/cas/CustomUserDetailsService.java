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
package fr.recia.glc.security.cas;

import fr.recia.glc.db.dto.UserDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Authenticate a user from the database.
 */
@Service
@Slf4j
public class CustomUserDetailsService implements AuthenticationUserDetailsService<CasAssertionAuthenticationToken> {

  @Autowired
  private APersonneRepository<APersonne> aPersonneRepository;

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
    SimplePersonneDto internal = aPersonneRepository.findByPersonneUid(uid);
    UserDto user = new UserDto(
      internal.getUid(),
      internal.getEtat().equals(Etat.Valide),
      true,
      true
    );
    Collection<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add((GrantedAuthority) () -> AuthoritiesConstants.USER);

    return new CustomUserDetails(user, authorities);
  }

}
