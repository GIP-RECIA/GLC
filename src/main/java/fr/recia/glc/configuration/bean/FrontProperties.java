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

import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.utils.ListUtils;
import lombok.Data;

import java.util.List;

@Data
public class FrontProperties {

  private Long endFunctionWarning;
  private StaffProperties staff;
  private List<Etat> editAllowedStates;
  private List<Etat> filterAccountStates;
  private ExtendedUportalHeaderProperties extendedUportalHeader;
  private ExtendedUportalFooterProperties extendedUportalFooter;
  private List<LoginOfficeProperties> loginOffices;

  @Data
  public static class StaffProperties {

    private CategoriePersonne teaching;
    private CategoriePersonne school;
    private CategoriePersonne collectivity;
    private CategoriePersonne academic;

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\"teaching\": \"" + teaching + "\"," +
        "\n\t\t\"school\": \"" + school + "\"," +
        "\n\t\t\"collectivity\": \"" + collectivity + "\"," +
        "\n\t\t\"academic\": \"" + academic + "\"" +
        "\n\t}";
    }

  }

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

  @Data
  public static class LoginOfficeProperties {

    private String source;
    private List<GuichetProperties> guichets;

    @Data
    public static class GuichetProperties {

      private String nom;
      private List<CategoriePersonne> categoriesPersonne;

      @Override
      public String toString() {
        return "\t\t\t\t{" +
          "\n\t\t\t\t\t\"nom\": \"" + nom + "\"," +
          "\n\t\t\t\t\t\"categoriesPersonne\": " + ListUtils.toStringList(categoriesPersonne) +
          "\n\t\t\t\t}";
      }

    }

    @Override
    public String toString() {
      return "\t\t{" +
        "\n\t\t\t\"source\": \"" + source + "\"," +
        "\n\t\t\t\"guichets\": " + ListUtils.toStringList(guichets, ",\n", "[\n", "\n\t\t\t]") +
        "\n\t\t}";
    }

  }

  @Override
  public String toString() {
    return "\"FrontProperties\": {" +
      "\n\t\"endFunctionWarning\": " + endFunctionWarning + "," +
      "\n\t\"staff\": " + staff + "," +
      "\n\t\"editAllowedStates\": " + ListUtils.toStringList(editAllowedStates) + "," +
      "\n\t\"filterAccountStates\": " + ListUtils.toStringList(filterAccountStates) + "," +
      "\n\t\"extendedUportalHeader\": " + extendedUportalHeader +
      "\n\t\"extendedUportalFooter\": " + extendedUportalFooter +
      "\n\t\"loginOffices\": " + ListUtils.toStringList(loginOffices, ",\n", "[\n", "\n\t]") +
      "\n}";
  }

}
