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

import fr.recia.glc.db.enums.CategorieStructure;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalGroupHelper {

  /**
   * The uid ldap attribute name.
   */
  private String groupIdAttribute;
  /**
   * The display name ldap attribute name.
   */
  private String groupDisplayNameAttribute;
  /**
   * The user search attribute name.
   */
  private String groupSearchAttribute;
  private String groupMembersAttribute;
  private Pattern groupKeyMemberRegex;
  private int groupKeyMemberIndex;
  private Pattern userKeyMemberRegex;
  private int userKeyMemberIndex;
  private Pattern groupDisplayNameRegex;
  private boolean groupDNContainsDisplayName;
  private boolean groupResolveUserMember;
  private boolean groupResolveUserMemberByUserAttributes;
  private Pattern groupsPatternWithoutMembersResolving;
  /**
   * The other attributes desired to show, facultative
   */
  private Set<String> otherGroupDisplayedAttributes;
  private String groupDNSubPath;

  private Pattern structureFromGroupPattern = Pattern.compile("([^:]+):([^:]+):(([^:_]+)(_(\\d{7}[^:]+))?):Tous_[^:]+");
  private String filterGroupsOfStructure = "(&(|(cn=*:Etablissements:*:tous*)(cn=*:Services_Academique:*:tous*))(!(cn=*:*:*:*:*)))";
  private Map<CategorieStructure, Pattern> structureCategoriesPatterns = Map.ofEntries(
    Map.entry(CategorieStructure.Etablissement,  Pattern.compile("((esco)|(clg[0-9]{2})|(agri)|(cfa)|(ef2s)):.*")),
    Map.entry(CategorieStructure.Collectivite_locale, Pattern.compile("coll:.*")),
    Map.entry(CategorieStructure.Service_academique, Pattern.compile("acad:.*"))
  );

  public ExternalGroupHelper(
    String groupIdAttribute, String groupDisplayNameAttribute, String groupSearchAttribute, String groupMembersAttribute,
    Pattern groupKeyMemberRegex, int groupKeyMemberIndex, Pattern userKeyMemberRegex, int userKeyMemberIndex,
    Pattern groupDisplayNameRegex, boolean groupDNContainsDisplayName, boolean groupResolveUserMember,
    boolean groupResolveUserMemberByUserAttributes, Pattern groupsPatternWithoutMembersResolving,
    Set<String> otherGroupDisplayedAttributes, String groupDNSubPath) {

    this.groupIdAttribute = groupIdAttribute;
    this.groupDisplayNameAttribute = groupDisplayNameAttribute;
    this.groupSearchAttribute = groupSearchAttribute;
    this.groupMembersAttribute = groupMembersAttribute;
    this.groupKeyMemberRegex = groupKeyMemberRegex;
    this.groupKeyMemberIndex = groupKeyMemberIndex;
    this.userKeyMemberRegex = userKeyMemberRegex;
    this.userKeyMemberIndex = userKeyMemberIndex;
    this.groupDisplayNameRegex = groupDisplayNameRegex;
    this.groupDNContainsDisplayName = groupDNContainsDisplayName;
    this.groupResolveUserMember = groupResolveUserMember;
    this.groupResolveUserMemberByUserAttributes = groupResolveUserMemberByUserAttributes;
    this.groupsPatternWithoutMembersResolving = groupsPatternWithoutMembersResolving;
    this.otherGroupDisplayedAttributes = otherGroupDisplayedAttributes;
    this.groupDNSubPath = groupDNSubPath;
  }

  public Set<String> getAttributes() {
    Set<String> set = new HashSet<>(otherGroupDisplayedAttributes);
    set.add(groupIdAttribute);
    set.add(groupDisplayNameAttribute);
    set.add(groupSearchAttribute);
    set.add(groupMembersAttribute);
    return set;
  }

  // used to tell if use match or get group of pattern
  public boolean isFormattingDisplayName() {
    return groupDisplayNameRegex != null;
  }

  // used to tell if use match or get group of pattern
  public boolean isExtractGroupMembers() {
    return groupKeyMemberIndex > 0;
  }

  // used to tell if use match or get group of pattern
  public boolean isExtractUserMembers() {
    return userKeyMemberIndex > 0;
  }

}
