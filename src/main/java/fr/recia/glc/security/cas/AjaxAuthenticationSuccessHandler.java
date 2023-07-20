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

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security success handler, specialized for Ajax requests.
 */
@Component
@Slf4j
public class AjaxAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  public AjaxAuthenticationSuccessHandler() {
    setDefaultTargetUrl("/");
    setTargetUrlParameter("spring-security-redirect");
  }

  @Override
  protected void handle(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException {
    String targetUrl = this.determineTargetUrl(request, response);
    if (response.isCommitted())
      log.debug("Response has already been committed. Unable to redirect to {}", targetUrl);
    else response.sendRedirect(targetUrl);
  }

  @Override
  public void onAuthenticationSuccess(
    HttpServletRequest request,
    HttpServletResponse response,
    Authentication authentication
  ) throws IOException, ServletException {
    this.handle(request, response, authentication);
    this.clearAuthenticationAttributes(request);
  }

}
