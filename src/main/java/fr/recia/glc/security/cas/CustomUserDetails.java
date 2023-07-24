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
package fr.recia.glc.security.cas;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.recia.glc.security.admingroup.Droit2GroupNameMap;
import fr.recia.glc.security.admingroup.Droit2StructureMap;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

  private static final long serialVersionUID = -4777124807325532850L;

  private String user;
  @JsonIgnore
  private Collection<? extends GrantedAuthority> authorities;
  private List<String> roles;
  @Setter
  private String sessionId;

  private final Droit2StructureMap droit2StructureMap = new Droit2StructureMap();
  private final Droit2GroupNameMap droit2GroupNameMap = new Droit2GroupNameMap();

  public CustomUserDetails() {
    super();
  }

  /**
   * @param user
   * @param authorities
   */
  public CustomUserDetails(String user, Collection<? extends GrantedAuthority> authorities) {
    super();
    this.user = user;
    this.authorities = authorities;
    this.roles = new ArrayList<>();
    for (GrantedAuthority authority : authorities) {
      this.roles.add(authority.getAuthority());
    }
  }

  @Override
  public String getPassword() {
    return null;
  }

  @Override
  public String getUsername() {
    return user;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }


  public boolean isAuthorizedStructure() {
    return false;
  }

  @Override
  public String toString() {
    return "UserDetails [user=" + user
      + ", authorities=" + roles.toString()
      + ", role= " + roles
      + ", isAccountNonExpired()=" + isAccountNonExpired()
      + ", isAccountNonLocked()=" + isAccountNonLocked()
      + ", isCredentialsNonExpired()=" + isCredentialsNonExpired()
      + ", isEnabled()=" + isEnabled() + "]";
  }

}
