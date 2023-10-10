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
package fr.recia.glc.services.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@AllArgsConstructor
@Slf4j
public class ServiceUrlHelper {

  @NonNull
  private String contextPath;

  @NonNull
  private List<String> authorizedDomainNames;

  @NonNull
  private String protocol = "https://";

  @NonNull
  private String itemUri;

  /**
   * Used for conf display only
   */
  private String getItemUrl() {
    return protocol + authorizedDomainNames.get(0) + contextPath + itemUri + "ID";
  }

  public String getRootAppUrl(final HttpServletRequest request) {
    final String contextPath = !request.getContextPath().isEmpty() ? request.getContextPath() + "/" : "/";

    return getRootDomainUrl(request) + contextPath;
  }

  public String getRootDomainUrl(final HttpServletRequest request) {
    final String url = request.getRequestURL().toString();
    final String uri = request.getRequestURI();

    return url.substring(0, url.length() - uri.length());
  }

  @Override
  public String toString() {
    return "ServiceUrlHelper {" +
      "contextPath='" + contextPath + '\'' +
      ", authorizedDomainNames='" + authorizedDomainNames + '\'' +
      ", protocol='" + protocol + '\'' +
      ", itemUri='" + itemUri + '\'' +
      ", example of itemUrl generated ='" + getItemUrl() + '\'' +
      "}";
  }

}
