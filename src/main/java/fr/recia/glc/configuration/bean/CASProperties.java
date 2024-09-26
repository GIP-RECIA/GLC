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
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Data
@Validated
public class CASProperties {

  @NotEmpty
  private String urlPrefix;
  @NotEmpty
  private String urlLogin;
  private String urlLogout;

  @Override
  public String toString() {
    return "CASProperties\": {" +
      "\n\t\"urlPrefix\": \"" + urlPrefix + "\"," +
      "\n\t\"urlLogin\": \"" + urlLogin + "\"," +
      "\n\t\"urlLogout\": \"" + urlLogout + "\"" +
      "\n}";
  }

}
