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
package fr.recia.glc.pojo;

import lombok.Data;

import java.util.List;

@Data
public class JsonAdditionalFonctionOldBody {

  private Long structureId;
  private List<String> toAddFunctions;
  private List<String> toDeleteFunctions;
  private String requiredAction;
  private String additionalCode;

  public boolean postDataOk() {
    final boolean standard = (toAddFunctions != null && !toAddFunctions.isEmpty()) || (toDeleteFunctions != null && !toDeleteFunctions.isEmpty());

    return (structureId != null && (standard || additionalCode != null) && requiredAction != null);
  }

}
