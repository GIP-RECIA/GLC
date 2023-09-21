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

import fr.recia.glc.services.beans.ServiceUrlHelper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

  private SecurityUtils() {
  }

  /**
   * Get the login of the current user.
   */
  public static String getCurrentLogin() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();

    if (authentication != null) {
      if (authentication.getPrincipal() instanceof UserDetails) {
        return ((UserDetails) authentication.getPrincipal()).getUsername();
      } else if (authentication.getPrincipal() instanceof String) {
        return (String) authentication.getPrincipal();
      }
    }

    return null;
  }

  /**
   * Get the User object of the current user.
   */
  public static CustomUserDetails getCurrentUserDetails() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();

    if (authentication != null) {
      if (authentication.getPrincipal() instanceof CustomUserDetails) {
        return (CustomUserDetails) authentication.getPrincipal();
      }
    }

    return null;
  }

  /**
   * Check if a user is authenticated.
   *
   * @return true if the user is authenticated, false otherwise
   */
  public static boolean isAuthenticated() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    final Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();

    return authorities != null
      && !authorities.equals(AuthorityUtils.NO_AUTHORITIES)
      && !authorities.contains(new SimpleGrantedAuthority(AuthoritiesConstants.ANONYMOUS));
  }

  public static String makeDynamicCASServiceUrl(ServiceUrlHelper urlHelper, HttpServletRequest request) {
    String urlBase = urlHelper.getRootAppUrl(request);
    for (String url : urlHelper.getAuthorizedDomainNames()) {
      if (urlBase.startsWith(url)) return urlBase;
    }
    throw new AccessDeniedException("The server is unable to authenticate from requested url " + urlBase);
  }

}
