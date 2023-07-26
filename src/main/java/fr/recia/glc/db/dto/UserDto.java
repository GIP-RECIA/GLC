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
package fr.recia.glc.db.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class UserDto implements Serializable {

  private String uid;
  private String displayName;
  private boolean enabled;
  private String email;
  private boolean foundOnInternalSource;
  private boolean foundOnExternalSource;

  public UserDto(String uid, boolean enabled, boolean foundOnInternalSource, boolean foundOnExternalSource) {
    this.uid = uid;
    this.enabled = enabled;
    this.foundOnInternalSource = foundOnInternalSource;
    this.foundOnExternalSource = foundOnExternalSource;
  }

}
