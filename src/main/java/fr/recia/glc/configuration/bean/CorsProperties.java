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
public class CorsProperties {

  private boolean enable;
  private boolean allowCredentials;
  private List<String> allowedOrigins;
  private List<String> exposedHeaders;
  private List<String> allowedHeaders;
  private List<String> allowedMethods;

  @Override
  public String toString() {
    return "{" +
      "\n\t\"CorsProperties\": {" +
      "\n\t\t\"enable\": " + enable + "," +
      "\n\t\t\"allowCredentials\": " + allowCredentials + "," +
      "\n\t\t\"allowedOrigins\": " + allowedOrigins.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX)) + "," +
      "\n\t\t\"exposedHeaders\": " + exposedHeaders.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX)) + "," +
      "\n\t\t\"allowedHeaders\": " + allowedHeaders.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX)) + "," +
      "\n\t\t\"allowedMethods\": " + allowedMethods.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX)) +
      "\n\t}" +
      "\n}";
  }

}
