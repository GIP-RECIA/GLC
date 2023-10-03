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
package fr.recia.glc.ldap.enums;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author GIP RECIA - Julien Gribonvald 14 juin 2012
 */
// We should persist these entries in a specific table.
@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = PermissionTypeDeserializer.class)
public enum PermissionType {

  // Warning the order should be greater rights to lessor's
  /**
   * Admin.
   */
  ADMIN(1, "ADMIN", 128, "enum.permission.superAdm.title"),
  /**
   * Manager.
   */
  MANAGER(2, "MANAGER", 64, "enum.permission.manager.title"),
  MANAGER_BRANCH(3, "MANAGER_BRANCH", 64, "enum.permission.manager.title"),
  /**
   * No Permission expect to look over the object and go on his childs.
   */
  LOOKOVER(4, "LOOKOVER", 0, "enum.permission.lookover.title"),
  LOOKOVER_BRANCH(5, "LOOKOVER_BRANCH", 0, "enum.permission.lookover.title");
//  /**
//   * User.
//   */
//  USER(8, "USER", 8, "permission.user.desc"),
//  /**
//   * Authenticated User And Without Permission.
//   */
//  AUTHENTICATED(9, "AUTHENTICATED", 4, "permission.authenticated.desc"),
//  /**
//   * UnAuthenticated Users
//   */
//  ANONYMOUS(10, "ANONYMOUS", 0, "permission.anonymous.desc");

  /**
   * Identifier.
   */
  private int id;
  /**
   * Name
   */
  private String name;
  /**
   * Mask.
   */
  private int mask;
  /**
   * The I18N key.
   */
  private String label;

  public static PermissionType fromName(final String name) {
    if (name != null) {
      for (PermissionType val : PermissionType.values()) {
        if (name.equalsIgnoreCase(val.toString())) return val;
      }
    }
    return null;
  }

  /**
   * Retrive the Enum Value from identifier.
   *
   * @param id
   * @return Role
   */
  public static PermissionType valueOf(final int id) {
    /*
     * if (id == PermissionType.USER.getId()) { return PermissionType.USER;
     * } else
     */
    if (id == PermissionType.LOOKOVER_BRANCH.getId()) {
      return PermissionType.LOOKOVER_BRANCH;
    } else if (id == PermissionType.LOOKOVER.getId()) {
      return PermissionType.LOOKOVER;
    } else if (id == PermissionType.MANAGER_BRANCH.getId()) {
      return PermissionType.MANAGER_BRANCH;
    } else if (id == PermissionType.MANAGER.getId()) {
      return PermissionType.MANAGER;
    } else if (id == PermissionType.ADMIN.getId()) {
      return PermissionType.ADMIN;
//    } else if (id == PermissionType.AUTHENTICATED.getId()) {
//      return PermissionType.AUTHENTICATED;
//    } else if (id == PermissionType.ANONYMOUS.getId()) {
//      return PermissionType.ANONYMOUS;
    } else
      return null;
  }

  @Override
  public String toString() {
    return this.name();
  }

}
