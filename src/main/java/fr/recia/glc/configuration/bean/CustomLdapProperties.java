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
package fr.recia.glc.configuration.bean;

import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.util.ListUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Nullable;
import javax.naming.directory.SearchControls;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.JSON_ARRAY_DELIMITER;
import static fr.recia.glc.configuration.Constants.JSON_ARRAY_PREFIX;
import static fr.recia.glc.configuration.Constants.JSON_ARRAY_SUFFIX;

@Data
@Validated
public class CustomLdapProperties {

  @NotNull
  private ContextSourceProperties contextSource = new ContextSourceProperties();
  @NotNull
  private LdapTemplateProperties ldapTemplate = new LdapTemplateProperties();
  @NotNull
  private BranchProperties userBranch = new BranchProperties();
  @Nullable
  private GroupBranchProperties groupBranch = new GroupBranchProperties();

  @Data
  @Validated
  public static class ContextSourceProperties {
    @NotEmpty
    private String[] urls;
    @NotBlank
    private String base;
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private boolean anonymousReadOnly = false;
    private boolean nativePooling = false;

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\"urls\": " + ListUtil.toStringList(Arrays.asList(urls), JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) + "," +
        "\n\t\t\"base\": \"" + base + "\"," +
        "\n\t\t\"username\": \"" + username + "\"," +
        "\n\t\t\"password\": \"*******\"," +
        "\n\t\t\"anonymousReadOnly\": \"" + anonymousReadOnly + "\"," +
        "\n\t\t\"nativePooling\": \"" + nativePooling + "\"" +
        "\n\t}";
    }
  }

  @Data
  public static class LdapTemplateProperties {
    private boolean ignorePartialResultException = false;
    private boolean ignoreNameNotFoundException = false;
    private boolean ignoreSizeLimitExceededException = true;
    private int searchScope = SearchControls.SUBTREE_SCOPE;
    private int timeLimit = 0;
    private int countLimit = 0;

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\"ignorePartialResultException\": \"" + ignorePartialResultException + "\"," +
        "\n\t\t\"ignoreNameNotFoundException\": \"" + ignoreNameNotFoundException + "\"," +
        "\n\t\t\"ignoreSizeLimitExceededException\": \"" + ignoreSizeLimitExceededException + "\"," +
        "\n\t\t\"searchScope\": \"" + searchScope + "\"," +
        "\n\t\t\"timeLimit\": \"" + timeLimit + "\"," +
        "\n\t\t\"countLimit\": \"" + countLimit + "\"" +
        "\n\t}";
    }
  }

  @Data
  @Validated
  public static class BranchProperties {
    @NotBlank
    private String baseDN = "ou=people";
    @NotBlank
    private String idAttribute = "uid";
    @NotBlank
    private String displayNameAttribute = "displayName";
    @NotBlank
    private String mailAttribute = "mail";
    @NotBlank
    private String searchAttribute = "cn";
    @NotBlank
    private String groupAttribute = "isMemberOf";
    @NotNull
    private Set<String> otherDisplayedAttributes = new HashSet<>();
    @NotNull
    private Set<String> otherBackendAttributes = new HashSet<>();

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\"baseDN\": \"" + baseDN + "\"," +
        "\n\t\t\"idAttribute\": \"" + idAttribute + "\"," +
        "\n\t\t\"displayNameAttribute\": \"" + displayNameAttribute + "\"," +
        "\n\t\t\"mailAttribute\": \"" + mailAttribute + "\"," +
        "\n\t\t\"searchAttribute\": \"" + searchAttribute + "\"," +
        "\n\t\t\"groupAttribute\": \"" + groupAttribute + "\"," +
        "\n\t\t\"otherDisplayedAttributes\": " + (otherDisplayedAttributes != null ? ListUtil.toStringList(otherDisplayedAttributes, JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) : null) + "," +
        "\n\t\t\"otherBackendAttributes\": " + (otherBackendAttributes != null ? ListUtil.toStringList(otherBackendAttributes, JSON_ARRAY_DELIMITER, JSON_ARRAY_PREFIX, JSON_ARRAY_SUFFIX) : null) +
        "\n\t}";
    }
  }

  @Getter
  @Setter
  @Validated
  public static class GroupBranchProperties extends BranchProperties {

    @NotNull
    private Pattern groupMemberKeyPattern;
    private int groupMemberKeyPatternIndex = 0;
    @NotNull
    private Pattern userMemberKeyPattern;
    private int userMemberKeyPatternIndex = 0;
    private Pattern groupDisplayNamePattern;
    private boolean DNContainsDisplayName = false;
    private boolean resolveUserMembers = false;
    private boolean resolveUserMembersByUserAttributes = true;
    private List<GroupDesignerProperties> designers;
    private List<GroupRegexProperties> nameFormatters;
    private Pattern dontResolveMembersWithGroupPattern;
    @NotNull
    private StructureProperties structureProperties;

    public GroupBranchProperties() {
      this.setBaseDN("ou=groups");
      this.setGroupAttribute("member");
      this.setIdAttribute("cn");
      this.setDisplayNameAttribute("cn");
    }

    @Data
    @Validated
    public static class StructureProperties {

      private Pattern structureFromGroupPattern;
      private String filterGroupsOfStructure;
      private Map<CategorieStructure, Pattern> structureCategoriesPatterns;
      private Pattern uaiPattern;

      @Override
      public String toString() {
        return "{" +
          "\n\t\t\t\"structureFromGroupPattern\": \"" + structureFromGroupPattern + "\"," +
          "\n\t\t\t\"filterGroupsOfStructure\": \"" + filterGroupsOfStructure + "\"," +
          "\n\t\t\t\"structureCategoriesPatterns\": " + structureCategoriesPatterns.keySet().stream()
          .map(key -> "\"" + key + "\": \"" + structureCategoriesPatterns.get(key) + "\"")
          .collect(Collectors.joining(",\n\t\t\t\t", "{\n\t\t\t\t", "\n\t\t\t}")) + "," +
          "\n\t\t\t\"uaiPattern\": \"" + uaiPattern + "\"" +
          "\n\t\t}";
      }

    }

    @Override
    public String toString() {
      return "{" +
        "\n\t\t\"groupMemberKeyPattern\": \"" + groupMemberKeyPattern + "\"," +
        "\n\t\t\"groupMemberKeyPatternIndex\": \"" + groupMemberKeyPatternIndex + "\"," +
        "\n\t\t\"userMemberKeyPattern\": \"" + userMemberKeyPattern + "\"," +
        "\n\t\t\"userMemberKeyPatternIndex\": \"" + userMemberKeyPatternIndex + "\"," +
        "\n\t\t\"groupDisplayNamePattern\": \"" + groupDisplayNamePattern + "\"," +
        "\n\t\t\"DNContainsDisplayName\": \"" + DNContainsDisplayName + "\"," +
        "\n\t\t\"resolveUserMembers\": \"" + resolveUserMembers + "\"," +
        "\n\t\t\"resolveUserMembersByUserAttributes\": \"" + resolveUserMembersByUserAttributes + "\"," +
        "\n\t\t\"designers\": " + ListUtil.toStringList(designers, ",\n\t\t\t", "[\n\t\t\t", "\n\t\t]") + "," +
        "\n\t\t\"nameFormatters\": " + ListUtil.toStringList(nameFormatters, ",\n\t\t\t", "[\n\t\t\t", "\n\t\t]") + "," +
        "\n\t\t\"dontResolveMembersWithGroupPattern\": \"" + dontResolveMembersWithGroupPattern + "\"," +
        "\n\t\t\"structureProperties\": " + structureProperties +
        "\n\t}";
    }
  }

  @Override
  public String toString() {
    return "CustomLdapProperties\": {" +
      "\n\t\"contextSource\": " + contextSource + "," +
      "\n\t\"ldapTemplate\": " + ldapTemplate + "," +
      "\n\t\"userBranch\": " + userBranch + "," +
      "\n\t\"groupBranch\": " + groupBranch +
      "\n}";
  }

}
