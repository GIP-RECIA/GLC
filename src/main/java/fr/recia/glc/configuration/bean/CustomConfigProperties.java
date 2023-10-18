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

import java.util.List;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.JSON_ARRAY_DELIMITER;
import static fr.recia.glc.configuration.Constants.JSON_ARRAY_PREFIX;
import static fr.recia.glc.configuration.Constants.JSON_ARRAY_SUFFIX;

@Data
public class CustomConfigProperties {

  private List<String> filiereAdministrative;
  private List<String> filiereTeaching;
  private List<String> sourcesExternalAll;
  private List<String> sourcesExternal4login;

  @Override
  public String toString() {
    return "{" +
      "\n\t\"CustomConfigProperties\": {" +
      "\n\t\t\"administrative\": " + filiereAdministrative.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX)) + "," +
      "\n\t\t\"teaching\": " + filiereTeaching.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX)) + "," +
      "\n\t\t\"sourcesExternalAll\": " + sourcesExternalAll.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX)) + "," +
      "\n\t\t\"sourcesExternal4login\": " + sourcesExternal4login.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX)) +
      "\n\t}" +
      "\n}";
  }

}
