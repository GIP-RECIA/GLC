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

import fr.recia.glc.configuration.bean.validator.IpAddress;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.PROPERTIES_TO_JSON_DELIMITER;
import static fr.recia.glc.configuration.Constants.PROPERTIES_TO_JSON_PREFIX;
import static fr.recia.glc.configuration.Constants.PROPERTIES_TO_JSON_SUFFIX;

/**
 * Created by jgribonvald on 03/06/16.
 */
@Data
@Validated
public class IpRangeProperties {

  @NotEmpty
  @IpAddress
  private Set<String> ipRanges;

  @Override
  public String toString() {
    return "{\n\"IpRangeProperties\":{"
      + "\n \"ipRanges\":" + ipRanges.stream()
      .map(String::valueOf)
      .collect(Collectors.joining(PROPERTIES_TO_JSON_DELIMITER, PROPERTIES_TO_JSON_PREFIX, PROPERTIES_TO_JSON_SUFFIX))
      + "\n}\n}";
  }

}