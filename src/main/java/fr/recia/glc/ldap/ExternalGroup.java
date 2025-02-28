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

import com.google.common.collect.Sets;
import fr.recia.glc.ldap.enums.SubjectType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 11 juil. 2014
 */
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class ExternalGroup implements IExternalGroup {

  private String id;
  private String displayName;

  private Map<String, List<String>> attributes = new HashMap<>();

  private Set<String> groupMembers = Sets.newHashSet();
  private Set<String> userMembers = Sets.newHashSet();

  public ExternalGroup(String id, String displayName, Map<String, List<String>> attributes) {
    this.id = id;
    this.displayName = displayName;
    this.attributes = attributes;
  }

  public ExternalGroup() {
    super();
  }

  @Override
  public List<String> getAttribute(final String attr) {
    if (attributes.containsKey(attr))
      return attributes.get(attr);
    else if (attributes.containsKey(attr.toLowerCase()))
      return attributes.get(attr.toLowerCase());
    return null;
  }

  public boolean hasMembers() {
    return !groupMembers.isEmpty() || !userMembers.isEmpty();
  }

  @Override
  public SubjectKey getSubject() {
    return new SubjectKey(this.getId(), SubjectType.GROUP);
  }

  public String getId() {
    return this.id;
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public Map<String, List<String>> getAttributes() {
    return this.attributes;
  }

  public Set<String> getGroupMembers() {
    return this.groupMembers;
  }

  public Set<String> getUserMembers() {
    return this.userMembers;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public void setAttributes(Map<String, List<String>> attributes) {
    this.attributes = attributes;
  }

  public void setGroupMembers(Set<String> groupMembers) {
    this.groupMembers = groupMembers;
  }

  public void setUserMembers(Set<String> userMembers) {
    this.userMembers = userMembers;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof ExternalGroup)) return false;
    final ExternalGroup other = (ExternalGroup) o;
    if (!other.canEqual(this)) return false;
    final Object this$id = this.id;
    final Object other$id = other.id;
    if (!Objects.equals(this$id, other$id)) return false;
    final Object this$displayName = this.displayName;
    final Object other$displayName = other.displayName;
    if (!Objects.equals(this$displayName, other$displayName))
      return false;
    final Object this$attributes = this.attributes;
    final Object other$attributes = other.attributes;
    if (!Objects.equals(this$attributes, other$attributes))
      return false;
    final Object this$groupMembers = this.groupMembers;
    final Object other$groupMembers = other.groupMembers;
    if (!Objects.equals(this$groupMembers, other$groupMembers))
      return false;
    final Object this$userMembers = this.userMembers;
    final Object other$userMembers = other.userMembers;
    return Objects.equals(this$userMembers, other$userMembers);
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $id = this.id;
    result = result * PRIME + ($id == null ? 0 : $id.hashCode());
    final Object $displayName = this.displayName;
    result = result * PRIME + ($displayName == null ? 0 : $displayName.hashCode());
    final Object $attributes = this.attributes;
    result = result * PRIME + ($attributes == null ? 0 : $attributes.hashCode());
    final Object $groupMembers = this.groupMembers;
    result = result * PRIME + ($groupMembers == null ? 0 : $groupMembers.hashCode());
    final Object $userMembers = this.userMembers;
    result = result * PRIME + ($userMembers == null ? 0 : $userMembers.hashCode());
    return result;
  }

  protected boolean canEqual(Object other) {
    return other instanceof ExternalGroup;
  }

  public String toString() {
    return "ExternalGroup(id=" + this.id + ", displayName=" + this.displayName + ", attributes=" + this.attributes + ", groupMembers=" + this.groupMembers + ", userMembers=" + this.userMembers + ")";
  }

}
