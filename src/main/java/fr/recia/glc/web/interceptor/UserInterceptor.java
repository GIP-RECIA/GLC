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
package fr.recia.glc.web.interceptor;

import fr.recia.glc.security.AuthoritiesConstants;
import fr.recia.glc.security.CustomUserDetails;
import fr.recia.glc.security.SecurityUtils;
import fr.recia.glc.web.interceptor.bean.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
public class UserInterceptor implements HandlerInterceptor {

  private final UserHolder userHolder;

  public UserInterceptor(UserHolder userHolder) {
    this.userHolder = userHolder;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
    String path = request.getRequestURI().substring(request.getContextPath().length());

    if (path.startsWith("/api")) {
      List<String> excludedPaths = List.of("^/api/config$");
      if (excludedPaths.stream().anyMatch(path::matches)) {
        log.debug("Path {} start with /api but is excluded form UserInterceptor", path);

        return true;
      }
    } else return true;

    final CustomUserDetails user = SecurityUtils.getCurrentUserDetails();
    if (user == null) {
      log.trace("user is not authenticated -> throw an error to redirect on authentication");
      throw new AccessDeniedException("Access is denied to anonymous !");
    }

    userHolder.setAdmin(user.getRoles().contains(AuthoritiesConstants.ADMIN));

    return true;
  }

}
