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
package fr.recia.glc.web.dto;

import fr.recia.glc.ldap.enums.SubjectType;
import fr.recia.glc.ldap.utils.CstPropertiesLength;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

@ToString
public class UserDTO extends SubjectDTO {

  @Getter
  private String userId;
  @Getter
  private String email;
  @Getter
  @Setter
  @Size(min = 2, max = CstPropertiesLength.LANG)
  private String langKey;
  @Getter
  private Map<String, List<String>> attributes;
  @Getter
  private boolean foundOnInternalSource;

  // private List<String> roles;

  /**
   * Constructor à utiliser lors de la convertion d'un objet JPA.
   *
   * @param userId
   * @param displayName
   */
  public UserDTO(@NotNull final String userId, @NotNull final String displayName) {
    super(new SubjectKeyDTO(userId, SubjectType.PERSON), displayName, true);
    this.userId = userId;
  }

  /**
   * Constructor à utiliser lors de la convertion d'un objet JPA enregistré en
   * BD et en même temps de la convertion d'un objet ExternalSource.
   *
   * @param userId
   * @param displayName
   * @param email
   * @param attributes
   */
  public UserDTO(@NotNull final String userId,
                 @NotNull final String displayName, final String email,
                 @NotNull final Map<String, List<String>> attributes) {
    super(new SubjectKeyDTO(userId, SubjectType.PERSON), displayName, true);
    this.userId = userId;
    this.email = email;
    this.attributes = attributes;
    this.foundOnInternalSource = true;
  }
}
