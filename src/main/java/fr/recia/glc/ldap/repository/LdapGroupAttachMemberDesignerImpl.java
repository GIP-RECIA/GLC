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
package fr.recia.glc.ldap.repository;

import fr.recia.glc.ldap.ExternalGroupHelper;
import fr.recia.glc.ldap.IExternalGroup;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by jgribonvald on 11/06/15.
 * Conf that permit to attach a group (tree) not provided by root ldap search on tree group the specified group and his members
 */
@ToString
@Slf4j
public class LdapGroupAttachMemberDesignerImpl implements IGroupMemberDesigner {

  @NotNull
  private final ExternalGroupHelper externalGroupHelper;
  @NotNull
  private final String groupAttachEndMatch;
  @NotEmpty
  private final List<String> groupToAttachEndPattern;

  private final Pattern patternGroupIntoAttach;

  public LdapGroupAttachMemberDesignerImpl(
    @NotNull final ExternalGroupHelper externalGroupHelper, @NotNull final String groupRootPattern,
    @NotNull final String groupAttachEndMatch, @NotEmpty final List<String> groupToAttachEndPattern
  ) {
    this.externalGroupHelper = externalGroupHelper;
    this.groupAttachEndMatch = groupAttachEndMatch;
    this.groupToAttachEndPattern = groupToAttachEndPattern;
    this.patternGroupIntoAttach = Pattern.compile(groupRootPattern + this.groupAttachEndMatch);
  }

  public IExternalGroup designe(IExternalGroup group, final IExternalGroupDao externalGroupDao) {
    if (group == null) return null;

    Matcher profMatcher = patternGroupIntoAttach.matcher(group.getId());
    log.debug("Design for group id {} with matcher {}, and is matching {}", group.getId(), profMatcher, profMatcher.matches());
    if (profMatcher.matches()) {
      StringBuilder filter = new StringBuilder("(|");
      for (String endPattern : groupToAttachEndPattern) {
        filter
          .append("(")
          .append(externalGroupHelper.getGroupSearchAttribute())
          .append("=")
          .append(group.getId().replaceFirst(groupAttachEndMatch, endPattern))
          .append(")");
      }
      filter.append(")");
      log.debug(" ldap filter that will be used : {}", filter);
      final List<IExternalGroup> members = externalGroupDao.getGroupsWithFilter(filter.toString(), null, false);
      if (members != null) {
        for (IExternalGroup externalGroup : members) {
          log.debug("Designer adding to {} the member {}", group.getId(), externalGroup.getId());
          group.getGroupMembers().add(externalGroup.getId());
        }
      }
    }
    return group;
  }

  @Override
  public boolean isDesignerMatchGroup(String groupId) {
    if (groupId != null && !groupId.isEmpty())
      return patternGroupIntoAttach.matcher(groupId).matches();
    return false;
  }

  @PostConstruct
  public void debug() {
    log.debug("Configuration du bean: {}", this);
  }

}
