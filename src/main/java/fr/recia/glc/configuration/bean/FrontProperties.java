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
import java.util.Map;

@Data
public class FrontProperties {

  private Long endFunctionWarning;
  private StaffProperties staff;
  private List<Etat> editAllowedStates;
  private List<Etat> filterAccountStates;
  private String templateApiPath;
  private ExtendedUportalProperties extendedUportal;
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
  public static class ExtendedUportalProperties {

    private ComponentProperties header;
    private ComponentProperties footer;

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\"header\": " + header + "," +
        "\n\t\t\"footer\": " + footer +
        "\n\t}";
    }

    @Data
    public static class ComponentProperties {

      private String componentPath;
      private Map<String, String> props;

      @Override
      public String toString() {
        return "{" +
          "\n\t\t\t\"componentPath\": \"" + componentPath + "\"," +
          "\n\t\t\t\"props\": " + props +
          "\n\t\t}";
      }

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
      "\n\t\"templateApiPath\": \"" + templateApiPath + "\"," +
      "\n\t\"extendedUportal\": " + extendedUportal +
      "\n\t\"loginOffices\": " + ListUtils.toStringList(loginOffices, ",\n", "[\n", "\n\t]") +
      "\n}";
  }

}
