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
package fr.recia.glc.web.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * This filter is used in production, to serve static resources generated by "grunt build".
 * <p/>
 * <p>
 * It is configured to serve resources from the "dist" directory, which is the Grunt
 * destination directory.
 * </p>
 */
@Slf4j
public class StaticResourcesProductionFilter implements Filter {

  /**
   * Pattern pour les ressources statiques
   */
  private Pattern staticResourcesPattern = Pattern.compile("^/ui/(assets/.*)$");

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String contextPath = httpRequest.getContextPath();
    String requestURI = httpRequest.getRequestURI();
    requestURI = StringUtils.substringAfter(requestURI, contextPath);
    if (StringUtils.equals("/", requestURI)) {
      // Redirection permanaente vers /ui si on essaye d'accéder à /
      log.debug("Permanent redirection from / to /ui/");
      HttpServletResponse httpResponse = (HttpServletResponse) response;
      httpResponse.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
      httpResponse.setHeader("Location", contextPath + "/ui/");
    } else {
      if (StringUtils.equals("/ui/", requestURI) || !staticResourcesPattern.matcher(requestURI).matches()) {
        requestURI = "/ui/index.html";
      }
      String newURI = requestURI.replace("/ui/", "/dist/");
      log.debug("RequestDispatcher - setting newURI to {}", newURI);
      request.getRequestDispatcher(newURI).forward(request, response);
    }
  }

}