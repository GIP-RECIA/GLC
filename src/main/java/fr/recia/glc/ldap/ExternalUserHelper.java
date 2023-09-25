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
package fr.recia.glc.ldap;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalUserHelper {

  /**
   * The uid ldap attribute name.
   */
  private String userIdAttribute;

  /**
   * The display name ldap attribute name.
   */
  private String userDisplayNameAttribute;

  /**
   * The email ldap attribute name.
   */
  private String userEmailAttribute;

  /**
   * The user search attribute name.
   */
  private String userSearchAttribute;

  private String userGroupAttribute;

  /**
   * The other attributes desired for fonctional use, facultative
   */
  private Set<String> otherUserAttributes;

  /**
   * The other attributes desired to show, facultative
   */
  private Set<String> otherUserDisplayedAttributes;

  private String userDNSubPath;

  public Set<String> getAttributes() {
    Set<String> set = new HashSet<>();
    set.addAll(otherUserDisplayedAttributes);
    set.add(userIdAttribute);
    set.add(userDisplayNameAttribute);
    set.add(userEmailAttribute);
    set.add(userSearchAttribute);
    set.add(userGroupAttribute);
    set.addAll(otherUserAttributes);
    return set;
  }

}
