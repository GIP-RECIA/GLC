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
package fr.recia.glc.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class GLCUser extends User {

    private Map<GLCRole, Set<String>> rightsForEtabs;
    private Set<GLCRole> rightsForColl;

    public GLCUser(String username, String password, Collection<? extends GrantedAuthority> authorities, Map<GLCRole, Set<String>> rightsForEtabs, Set<GLCRole> rightsForColl) {
        super(username, password, authorities);
        this.rightsForEtabs = rightsForEtabs;
        this.rightsForColl = rightsForColl;
    }

    public GLCUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

}
