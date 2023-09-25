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

import com.google.common.collect.Sets;
import fr.recia.glc.ldap.ExternalGroupHelper;
import fr.recia.glc.ldap.IExternalGroup;
import fr.recia.glc.ldap.IExternalGroupDisplayNameFormatter;
import fr.recia.glc.ldap.IStructure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.HardcodedFilter;
import org.springframework.ldap.filter.WhitespaceWildcardsFilter;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.ldap.query.LdapQueryBuilder;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

/**
 * @author GIP RECIA - Julien Gribonvald 11 juil. 2014
 */
@Data
@AllArgsConstructor
@Slf4j
public class LdapGroupDaoImpl implements IExternalGroupDao {

  /**
   * Spring template used to perform search in the ldap.
   */
  //@Autowired
  private LdapTemplate ldapTemplate;

  //@Autowired
  private ExternalGroupHelper externalGroupHelper;

  //@Autowired
  private List<IExternalGroupDisplayNameFormatter> groupDisplayNameFormatters;

  //@Autowired
  private IExternalUserDao externalUserDao;

  //@Autowired
  private List<IGroupMemberDesigner> groupMemberDesigners;

  /**
   * constructor.
   */
  public LdapGroupDaoImpl() {
    super();
  }

  @Override
  public Set<IStructure> getStructuresFromGroups() {
    HardcodedFilter filter = new HardcodedFilter(externalGroupHelper.getFilterGroupsOfStructure());
    if (log.isDebugEnabled()) {
      log.debug("LDAP filter applied {} ", filter.encode());
    }
    //ContextMapper<IExternalGroup> mapper = new LdapGroupStructureContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);
    ContextMapper<IStructure> mapper = new LdapGroupStructureContextMapper(this.externalGroupHelper);
    // SearchControls constraints = new SearchControls();
    // constraints.setReturningAttributes((String[])
    // ldapUserHelper.getAttributes().toArray());
    LdapQuery query = LdapQueryBuilder.query()
      .attributes(externalGroupHelper.getAttributes().toArray(new String[0]))
      .base(externalGroupHelper.getGroupDNSubPath()).filter(filter);
    return Sets.newHashSet(ldapTemplate.search(query, mapper));
  }

  //  @Override
//  //@Cacheable(value = "ExternalGroups", key = "#id")
//  public IExternalGroup getGroupById(@NotNull final String id, final boolean withMembers) {
//    AndFilter filter = new AndFilter();
//    filter.append(new EqualsFilter(externalGroupHelper.getGroupIdAttribute(), id));
//    List<IExternalGroup> groups = this.searchWithFilter(filter, withMembers);
//    if (groups.isEmpty() || groups.size() > 1) {
//      log.error("The search with " + filter.encode()
//        + " returned a bad result, only one entry should be returned ! Result: " + groups);
//      return null;
//    }
//    if (log.isDebugEnabled()) {
//      log.debug("LDAP group found : {}", groups.iterator().next());
//    }
//    return groups.iterator().next();
//  }
//
//  @Override
//  //@Cacheable(value = "ExternalGroups", key = "#id")
//  public List<IExternalGroup> getGroupsById(@NotNull final Collection<String> ids, final boolean withMembers) {
//    if (ids == null || ids.isEmpty())
//      return Lists.newArrayList();
//    AndFilter filter = new AndFilter();
//    OrFilter list = new OrFilter();
//    for (String id : ids) {
//      list.append(new EqualsFilter(externalGroupHelper.getGroupIdAttribute(), id));
//    }
//    filter.append(list);
//    if (log.isDebugEnabled()) {
//      log.debug("getGroupsById LDAP filter applied : {}", filter.encode());
//    }
//    return this.searchWithFilter(filter, withMembers);
//  }
//
//  @Override
//  //@Cacheable(value = "ExternalGroups", key = "#id")
//  public List<IExternalGroup> getGroupsByIdStartWith(@NotNull final Collection<String> ids, final boolean withMembers) {
//    if (ids == null || ids.isEmpty())
//      return Lists.newArrayList();
//    AndFilter filter = new AndFilter();
//    OrFilter list = new OrFilter();
//    for (String id : ids) {
//      list.append(new EqualsFilter(externalGroupHelper.getGroupIdAttribute(), id));
//      list.append(new LikeFilter(externalGroupHelper.getGroupIdAttribute(), id.trim() + ":*"));
//    }
//    filter.append(list);
//    if (log.isDebugEnabled()) {
//      log.debug("getGroupsByIdStartWith LDAP filter applied : {}", filter.encode());
//    }
//    return this.searchWithFilter(filter, withMembers);
//  }
//
//  @Override
//  public List<IExternalGroup> getDirectGroupMembers(@NotNull final String id, final boolean withMembers) {
//    IExternalGroup group = getGroupById(id, true);
//    if (group != null && group.hasMembers()) {
//      return getGroupsById(group.getGroupMembers(), withMembers);
//    }
//    return Lists.newArrayList();
//  }

  @Override
  // @Cacheable(value = "ExternalGroups")
  public List<IExternalGroup> getGroupsWithFilter(@NotNull String stringFilter, String token, final boolean withMembers) {
    AndFilter filter = new AndFilter()
      .and(new HardcodedFilter(stringFilter));
    if (token != null && !token.isEmpty()) {
      filter.and(new WhitespaceWildcardsFilter(externalGroupHelper.getGroupSearchAttribute(), token));
    }
    log.debug("getGroupsWithFilter LDAP filter {}, token {}, withMembers {}", stringFilter, token, withMembers);
    return searchWithFilter(filter, withMembers);
  }

//  @Override
//  public List<IExternalUser> getDirectUserMembers(@NotNull final String id) {
//    if (!externalGroupHelper.isGroupResolveUserMember()) return Lists.newArrayList();
//    if (externalGroupHelper.isGroupResolveUserMemberByUserAttributes()) {
//      return externalUserDao.getUsersByGroupId(id);
//    }
//    //else
//    IExternalGroup group = getGroupById(id, true);
//    if (group != null && group.hasMembers() && !group.getUserMembers().isEmpty())
//      return externalUserDao.getUsersByUids(group.getUserMembers());
//    return Lists.newArrayList();
//  }
//
//  @Override
//  public boolean isGroupMemberOfGroup(@NotNull final String member, @NotNull final String parent) {
//    if (externalGroupHelper.getGroupKeyMemberRegex() == null) return false;
//    // direct resolution when using grouper subpath is in parent path, so do only char comparison.
//    if (member.startsWith(parent)) return true;
//    // else if not subpath checking in ldap
//    AndFilter filter = new AndFilter().and(new LikeFilter(externalGroupHelper.getGroupSearchAttribute(), parent + "*"));
//    filter.and(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), externalGroupHelper.getGroupKeyMemberRegex().toString().replace("(.*)", member)));
//    if (log.isDebugEnabled()) {
//      log.debug("isGroupMemberOfGroup LDAP filter applied : {}", filter);
//    }
//
//    boolean found = !searchWithFilter(filter, false).isEmpty();
//    // FIX: to watch on group where applied designers
//    if (!found) {
//      List<IExternalGroup> groups = searchWithFilter(new LikeFilter(externalGroupHelper.getGroupSearchAttribute(), parent + "*"), true);
//      for (IExternalGroup gp : groups) {
//        if (gp.getGroupMembers().contains(member)) return true;
//      }
//    }
//
//    return found;
//  }
//
//  @Override
//  public boolean isGroupMemberOfGroupFilter(@NotNull String stringFilter, @NotNull final String member) {
//    log.debug("isGroupMemberOfGroupFilter LDAP filter {} and group {}", stringFilter, member);
//    AndFilter filter = new AndFilter()
//      .and(new HardcodedFilter(stringFilter));
//    OrFilter or = new OrFilter();
//    or.append(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), externalGroupHelper.getGroupKeyMemberRegex().toString().replace("(.*)", member)));
//    or.append(new EqualsFilter(externalGroupHelper.getGroupSearchAttribute(), member));
//    filter.and(or);
//
//    boolean found = !searchWithFilter(filter, false).isEmpty();
//    // FIX : to watch on applied designers
//    if (!found) {
//      List<IExternalGroup> groups = searchWithFilter(new HardcodedFilter(stringFilter), true);
//      for (IExternalGroup gp : groups) {
//        if (gp.getGroupMembers().contains(member)) return true;
//        // looking for childs of root
//        for (String child : gp.getGroupMembers()) {
//          boolean isDesigneApply = false;
//          for (IGroupMemberDesigner gpDesigner : groupMemberDesigners) {
//            if (gpDesigner.isDesignerMatchGroup(child)) {
//              isDesigneApply = true;
//            }
//          }
//          if (isDesigneApply) {
//            IExternalGroup designedGroup = this.getGroupById(child, true);
//            if (designedGroup.getGroupMembers().contains(member)) return true;
//          }
//        }
//      }
//    }
//
//    return found;
//
//  }
//
//  @Override
//  public boolean isUserMemberOfGroup(@NotNull final String uid, @NotNull final String group) {
//    if (externalGroupHelper.getUserKeyMemberRegex() == null) return false;
//    AndFilter filter = new AndFilter().and(new LikeFilter(externalGroupHelper.getGroupSearchAttribute(), group + "*"));
//    filter.and(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), externalGroupHelper.getUserKeyMemberRegex().toString().replace("(.*)", uid)));
//    if (log.isDebugEnabled()) {
//      log.debug("isUserMemberOfGroup LDAP filter applied : {}", filter);
//    }
//    return !searchWithFilter(filter, false).isEmpty();
//  }
//
//  @Override
//  public boolean isGroupMemberOfAtLeastOneGroup(@NotNull final String member, @NotNull final Iterable<String> parents) {
//    if (externalGroupHelper.getGroupKeyMemberRegex() == null) return false;
//    // direct resolution when using grouper subpath is in parent path, so do only char comparison.
//    if (OptimContainsGroup(member, parents)) return true;
//    // else if not subpath checking in ldap
//    final Filter filter = this.getFilterForAtLeastOneGroup(member, parents, externalGroupHelper.getGroupKeyMemberRegex());
//    if (log.isDebugEnabled()) {
//      log.debug("isGroupMemberOfGroup LDAP filter applied : {}", filter);
//    }
//    return !searchWithFilter(filter, false).isEmpty();
//  }
//
//  @Override
//  public boolean isUserMemberOfAtLeastOneGroup(@NotNull final String uid, @NotNull final Iterable<String> groups) {
//    if (externalGroupHelper.getUserKeyMemberRegex() == null) return false;
//    final Filter filter = this.getFilterForAtLeastOneGroup(uid, groups, externalGroupHelper.getUserKeyMemberRegex());
//    if (log.isDebugEnabled()) {
//      log.debug("isUserMemberOfAtLeastOneGroup LDAP filter applied : {}", filter);
//    }
//    return !searchWithFilter(filter, false).isEmpty();
//  }
//
//  private Filter getFilterForAtLeastOneGroup(@NotNull final String value, @NotNull final Iterable<String> groups, @NotNull Pattern pattern) {
//    OrFilter orFilter = new OrFilter();
//    for (final String group : groups) {
//      orFilter.append(new LikeFilter(externalGroupHelper.getGroupSearchAttribute(), group + "*"));
//    }
//    AndFilter filter = new AndFilter().and(orFilter);
//    filter.and(new EqualsFilter(externalGroupHelper.getGroupMembersAttribute(), pattern.toString().replace("(.*)", value)));
//    return filter;
//  }
//
  // @Cacheable(value = "ExternalGroups", key = "#filter")
  private List<IExternalGroup> searchWithFilter(@NotNull final Filter filter, final boolean withMembers) {
    if (log.isDebugEnabled()) {
      log.debug("LDAP filter applied {} and resolve members {} ", filter.encode(), withMembers);
    }
    ContextMapper<IExternalGroup> mapper = new LdapGroupContextMapper(this.externalGroupHelper, this.groupDisplayNameFormatters);

    // SearchControls constraints = new SearchControls();
    // constraints.setReturningAttributes((String[])
    // ldapUserHelper.getAttributes().toArray());
    LdapQuery query = LdapQueryBuilder.query()
      .attributes(externalGroupHelper.getAttributes().toArray(new String[0]))
      .base(externalGroupHelper.getGroupDNSubPath()).filter(filter);
    return ldapTemplate.search(query, mapper);
  }
//
//  private boolean OptimContainsGroup(@NotNull final String member, @NotNull final Iterable<String> parents) {
//    for (String group : parents) {
//      if (member.startsWith(group)) return true;
//    }
//    return false;
//  }
//
//  private List<IExternalGroup> applyDesigners(List<IExternalGroup> groups) {
//    List<IExternalGroup> tmp = Lists.newArrayList();
//    for (IExternalGroup group : groups) {
//      for (IGroupMemberDesigner gpDesigner : groupMemberDesigners) {
//        group = applyDesigners(group);
//      }
//      tmp.add(group);
//    }
//    return tmp;
//  }
//
//  private IExternalGroup applyDesigners(IExternalGroup group) {
//    for (IGroupMemberDesigner gpDesigner : groupMemberDesigners) {
//      group = gpDesigner.designe(group, this);
//    }
//    return group;
//  }

    /*private OrFilter orFilterOnIds(@NotNull final Iterable<String> ids) {
        // needed since empty OrFilter() is true instead of false
        // (https://jira.springsource.org/browse/LDAP-226)
        if (Iterables.isEmpty(ids))
            return null;

        final OrFilter filter = new OrFilter();
        for (String id : ids) {
            filter.or(new EqualsFilter(externalGroupHelper.getGroupIdAttribute(),id));
        }
        return filter;
    }*/

}
