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

import fr.recia.glc.db.dto.AlertType;
import fr.recia.glc.utils.ListUtils;
import lombok.Data;

import java.util.List;

@Data
public class CustomConfigProperties {

  private Integer suppressDays;
  private List<AlertProperties> alerts;

  @Data
  public static class AlertProperties {

    private String source;
    private List<FonctionAlertProperties> fonctionAlerts;


    @Data
    public static class FonctionAlertProperties {

      private String code;
      private ValueProperties min;
      private ValueProperties max;

      @Data
      public static class ValueProperties {

        private int value;
        private AlertType type;
        private boolean action;

        @Override
        public String toString() {
          return "{ " +
            "\"value\": " + value + ", " +
            "\"type\": \"" + type + "\", " +
            "\"action\": \"" + action +
            "\" }";
        }
      }

      @Override
      public String toString() {
        return "{" +
          "\n\t\t\t\t\t\"code\": \"" + code + "\"," +
          "\n\t\t\t\t\t\"min\": " + min + "," +
          "\n\t\t\t\t\t\"max\": " + max +
          "\n\t\t\t\t}";
      }

    }

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\t\"source\": \"" + source + "\"," +
        "\n\t\t\t\"fonctionAlerts\": " + ListUtils.toStringList(fonctionAlerts, ",\n\t\t\t\t", "[\n\t\t\t\t", "\n\t\t\t]") +
        "\n\t\t}";
    }

  }

  @Override
  public String toString() {
    return "CustomConfigProperties\": {" +
      "\n\t\"suppressDays\": " + suppressDays + "," +
      "\n\t\"alerts\": " + ListUtils.toStringList(alerts, ",\n\t", "[\n\t\t", "\n\t]") +
      "\n}";
  }

}
