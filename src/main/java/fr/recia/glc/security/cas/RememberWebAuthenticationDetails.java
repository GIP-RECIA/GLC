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

import fr.recia.glc.security.SecurityUtils;
import fr.recia.glc.services.beans.ServiceUrlHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.web.authentication.ServiceAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

@Slf4j
public class RememberWebAuthenticationDetails extends WebAuthenticationDetails implements ServiceAuthenticationDetails {

  private final String queryString;
  private final String serviceUrl;

  public RememberWebAuthenticationDetails(HttpServletRequest request, ServiceUrlHelper urlHelper, ServiceProperties serviceProperties, String casTargetUrlParam) {
    super(request);

    this.queryString = request.getQueryString();
    log.debug("Remember request {}", this.queryString);

    final String targetPath = this.getTargetPath(this.queryString, casTargetUrlParam);
    String serviceUrl = SecurityUtils.makeDynamicCASServiceUrl(urlHelper, request) + serviceProperties.getService();
    if (targetPath != null && !targetPath.isEmpty()) {
      this.serviceUrl = String.format("%s?%s", serviceUrl, targetPath);
    } else {
      this.serviceUrl = serviceUrl;
    }
    log.debug("Remember serviceUrl {}", this.serviceUrl);
  }

  /**
   * Extracts the original target url form the query string. Example query string:
   * spring-security-redirect=/widget.jsp&ticket=ST-112-RiRTVZmzghHO7az5gpJF-cas
   */
  private String getTargetPath(String queryString, String casTargetUrlParam) {
    String targetPath = "";
    if (queryString != null) {
      int start = queryString.indexOf(casTargetUrlParam);
      if (start >= 0) {
        int end = queryString.indexOf("&", start);
        if (end >= 0) {
          targetPath = queryString.substring(start, end);
        } else {
          targetPath = queryString.substring(start);
        }
      }
    }
    return targetPath;
  }

  public String getQueryString() {
    log.debug("Remember request get queryString {}", this.queryString);
    return this.queryString;
  }

  @Override
  public String getServiceUrl() {
    return this.serviceUrl;
  }

}
