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
package fr.recia.glc.configuration.bean;

import lombok.Data;

@Data
public class FrontProperties {

  private Long endFunctionWarning;
  private ExtendedUportalHeaderProperties extendedUportalHeader = new ExtendedUportalHeaderProperties();
  private ExtendedUportalFooterProperties extendedUportalFooter = new ExtendedUportalFooterProperties();

  @Data
  public static class ExtendedUportalHeaderProperties {

    private String componentPath;
    private String contextApiUrl;
    private String signOutUrl;
    private String defaultOrgLogoPath;
    private String defaultAvatarPath;
    private String defaultOrgIconPath;
    private String favoriteApiUrl;
    private String layoutApiUrl;
    private String organizationApiUrl;
    private String portletApiUrl;
    private String userInfoApiUrl;
    private String userInfoPortletUrl;
    private String sessionApiUrl;
    private String templateApiPath;
    private String switchOrgPortletUrl;
    private String favoritesPortletCardSize;
    private String gridPortletCardSize;
    private String hideActionMode;
    private String showFavoritesInSlider;
    private String returnHomeTitle;
    private String returnHomeTarget;
    private String iconType;

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\"componentPath\": \"" + componentPath + "\"," +
        "\n\t\t\"contextApiUrl\": \"" + contextApiUrl + "\"," +
        "\n\t\t\"signOutUrl\": \"" + signOutUrl + "\"," +
        "\n\t\t\"defaultOrgLogoPath\": \"" + defaultOrgLogoPath + "\"," +
        "\n\t\t\"defaultAvatarPath\": \"" + defaultAvatarPath + "\"," +
        "\n\t\t\"defaultOrgIconPath\": \"" + defaultOrgIconPath + "\"," +
        "\n\t\t\"favoriteApiUrl\": \"" + favoriteApiUrl + "\"," +
        "\n\t\t\"layoutApiUrl\": \"" + layoutApiUrl + "\"," +
        "\n\t\t\"organizationApiUrl\": \"" + organizationApiUrl + "\"," +
        "\n\t\t\"portletApiUrl\": \"" + portletApiUrl + "\"," +
        "\n\t\t\"userInfoApiUrl\": \"" + userInfoApiUrl + "\"," +
        "\n\t\t\"userInfoPortletUrl\": \"" + userInfoPortletUrl + "\"," +
        "\n\t\t\"sessionApiUrl\": \"" + sessionApiUrl + "\"," +
        "\n\t\t\"templateApiPath\": \"" + templateApiPath + "\"," +
        "\n\t\t\"switchOrgPortletUrl\": \"" + switchOrgPortletUrl + "\"," +
        "\n\t\t\"favoritesPortletCardSize\": \"" + favoritesPortletCardSize + "\"," +
        "\n\t\t\"gridPortletCardSize\": \"" + gridPortletCardSize + "\"," +
        "\n\t\t\"hideActionMode\": \"" + hideActionMode + "\"," +
        "\n\t\t\"showFavoritesInSlider\": \"" + showFavoritesInSlider + "\"," +
        "\n\t\t\"returnHomeTitle\": \"" + returnHomeTitle + "\"," +
        "\n\t\t\"returnHomeTarget\": \"" + returnHomeTarget + "\"," +
        "\n\t\t\"iconType\": \"" + iconType + "\"" +
        "\n\t}";
    }

  }

  @Data
  public static class ExtendedUportalFooterProperties {

    private String componentPath;
    private String templateApiPath;

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\"componentPath\": \"" + componentPath + "\"," +
        "\n\t\t\"templateApiPath\": \"" + templateApiPath + "\"" +
        "\n\t}";
    }

  }

  @Override
  public String toString() {
    return "\"FrontProperties\": {" +
      "\n\t\"endFunctionWarning\": \"" + endFunctionWarning + "\"," +
      "\n\t\"extendedUportalHeader\": \"" + extendedUportalHeader + "\"," +
      "\n\t\"extendedUportalFooter\": \"" + extendedUportalFooter + "\"" +
      "\n}";
  }

}
