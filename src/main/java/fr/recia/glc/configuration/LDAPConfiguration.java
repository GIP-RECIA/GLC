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
package fr.recia.glc.configuration;

import com.google.common.collect.Lists;
import fr.recia.glc.configuration.bean.CustomLdapProperties;
import fr.recia.glc.configuration.bean.GroupDesignerProperties;
import fr.recia.glc.configuration.bean.GroupRegexProperties;
import fr.recia.glc.ldap.ExternalGroupHelper;
import fr.recia.glc.ldap.ExternalUserHelper;
import fr.recia.glc.ldap.IExternalGroupDisplayNameFormatter;
import fr.recia.glc.ldap.repository.IExternalGroupDao;
import fr.recia.glc.ldap.repository.IExternalUserDao;
import fr.recia.glc.ldap.repository.IGroupMemberDesigner;
import fr.recia.glc.ldap.repository.LdapGroupAttachMemberDesignerImpl;
import fr.recia.glc.ldap.repository.LdapGroupDaoImpl;
import fr.recia.glc.ldap.repository.LdapGroupRegexpDisplayNameFormatter;
import fr.recia.glc.ldap.repository.LdapGroupRegexpDisplayNameFormatterESCO;
import fr.recia.glc.ldap.repository.LdapGroupRegexpDisplayNameFormatterESCOReplace;
import fr.recia.glc.services.beans.IStructureLoader;
import fr.recia.glc.services.beans.StructureLoaderImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.util.Assert;

import java.util.List;

@Configuration
@Slf4j
public class LDAPConfiguration {

  private final CustomLdapProperties ldapProperties;

  public LDAPConfiguration(GLCProperties glcProperties) {
    this.ldapProperties = glcProperties.getLdap();
  }

  @Bean
  public LdapContextSource contextSource() {
    log.debug("Configuring LdapContextSource");
    final LdapContextSource ldapCtx = new LdapContextSource();
    ldapCtx.setAnonymousReadOnly(ldapProperties.getContextSource().isAnonymousReadOnly());
    ldapCtx.setBase(ldapProperties.getContextSource().getBase());
    ldapCtx.setUrls(ldapProperties.getContextSource().getUrls());
    ldapCtx.setUserDn(ldapProperties.getContextSource().getUsername());
    ldapCtx.setPassword(ldapProperties.getContextSource().getPassword());
    ldapCtx.setPooled(ldapProperties.getContextSource().isNativePooling());
    log.debug("LDAPContext is configured with properties {}", ldapProperties.getContextSource());

    return ldapCtx;
  }

  @Bean
  public LdapTemplate ldapTemplate() {
    final LdapTemplate ldapTpl = new LdapTemplate();
    ldapTpl.setContextSource(contextSource());
    ldapTpl.setDefaultCountLimit(ldapProperties.getLdapTemplate().getCountLimit());
    ldapTpl.setDefaultTimeLimit(ldapProperties.getLdapTemplate().getTimeLimit());
    ldapTpl.setDefaultSearchScope(ldapProperties.getLdapTemplate().getSearchScope());
    ldapTpl.setIgnoreNameNotFoundException(ldapProperties.getLdapTemplate().isIgnoreNameNotFoundException());
    ldapTpl.setIgnorePartialResultException(ldapProperties.getLdapTemplate().isIgnorePartialResultException());
    ldapTpl.setIgnoreSizeLimitExceededException(ldapProperties.getLdapTemplate().isIgnoreSizeLimitExceededException());

    return ldapTpl;
  }

  @Bean
  public ExternalUserHelper externalUserHelper() {
    final ExternalUserHelper ldapUh = new ExternalUserHelper(
      ldapProperties.getUserBranch().getIdAttribute(),
      ldapProperties.getUserBranch().getDisplayNameAttribute(),
      ldapProperties.getUserBranch().getMailAttribute(),
      ldapProperties.getUserBranch().getSearchAttribute(),
      ldapProperties.getUserBranch().getGroupAttribute(),
      ldapProperties.getUserBranch().getOtherBackendAttributes(),
      ldapProperties.getUserBranch().getOtherDisplayedAttributes(),
      ldapProperties.getUserBranch().getBaseDN()
    );
    log.debug("LdapAttributes for Users configured : {}", ldapUh);

    return ldapUh;
  }

  @Bean
  public ExternalGroupHelper externalGroupHelper() {
    log.debug("Configure bean ExternalGroupHelper with LDAP attributes");
    Assert.notNull(
      ldapProperties.getGroupBranch(),
      "Use of LDAP group branch require 'app.ldap.groupBranch.*' properties configured !"
    );
    ExternalGroupHelper ldapUh = new ExternalGroupHelper(
      ldapProperties.getGroupBranch().getIdAttribute(),
      ldapProperties.getGroupBranch().getDisplayNameAttribute(),
      ldapProperties.getGroupBranch().getSearchAttribute(),
      ldapProperties.getGroupBranch().getGroupAttribute(),
      ldapProperties.getGroupBranch().getGroupMemberKeyPattern(),
      ldapProperties.getGroupBranch().getGroupMemberKeyPatternIndex(),
      ldapProperties.getGroupBranch().getUserMemberKeyPattern(),
      ldapProperties.getGroupBranch().getUserMemberKeyPatternIndex(),
      ldapProperties.getGroupBranch().getGroupDisplayNamePattern(),
      ldapProperties.getGroupBranch().isDNContainsDisplayName(),
      ldapProperties.getGroupBranch().isResolveUserMembers(),
      ldapProperties.getGroupBranch().isResolveUserMembersByUserAttributes(),
      ldapProperties.getGroupBranch().getDontResolveMembersWithGroupPattern(),
      ldapProperties.getGroupBranch().getOtherDisplayedAttributes(),
      ldapProperties.getGroupBranch().getBaseDN()
    );
    log.debug("LdapAttributes for Groups configured : {}", ldapUh);

    return ldapUh;
  }

  @Bean
  public IExternalGroupDao ldapExternalGroupDao(IExternalUserDao externalUserDao, LdapTemplate ldapTemplate) {
    log.debug("Configuring IExternalGroupDao with LDAP DAO");
    Assert.notNull(
      ldapProperties.getGroupBranch(),
      "Use of Group Branch require 'app.ldap.groupBranch.*' properties configured !"
    );
    List<IExternalGroupDisplayNameFormatter> formatters = Lists.newLinkedList();
    // should be run firstly !
    formatters.add(new LdapGroupRegexpDisplayNameFormatter(externalGroupHelper()));
    for (GroupRegexProperties grp : ldapProperties.getGroupBranch().getNameFormatters()) {
      formatters.add(new LdapGroupRegexpDisplayNameFormatterESCO(
        externalGroupHelper(),
        grp.getGroupMatcher(),
        grp.getGroupNameRegex(),
        grp.getGroupNameIndex(),
        grp.getGroupRecomposerSeparator(),
        grp.getGroupSuffixeToAppend()
      ));
    }
    //should be run at final
    formatters.add(new LdapGroupRegexpDisplayNameFormatterESCOReplace());

    List<IGroupMemberDesigner> designers = Lists.newArrayList();
    for (GroupDesignerProperties grp : ldapProperties.getGroupBranch().getDesigners()) {
      designers.add(new LdapGroupAttachMemberDesignerImpl(
        externalGroupHelper(),
        grp.getGroupRootPattern(),
        grp.getGroupAttachEndMatch(),
        grp.getGroupToAttachEndPattern()
      ));
    }

    return new LdapGroupDaoImpl(ldapTemplate, externalGroupHelper(), formatters, externalUserDao, designers, ldapProperties);
  }

  @Bean
  public IStructureLoader loadedStructure(IExternalGroupDao externalGroupDao) {
    log.debug("Loading Structure extracted from groups");
    IStructureLoader loader = new StructureLoaderImpl(externalGroupDao);

    return loader;
  }

//  @Bean
//  public IGroupService ldapGroupService(
//    IPermissionService permissionService, TreeJSDTOFactory treeJSDTOFactory, UserDTOFactory userDTOFactory,
//    SubscriberService subscriberService, FilterRepository filterRepository, ContextService contextService,
//    IExternalGroupDao externalGroupDao
//  ) {
//    return new GroupService(permissionService, treeJSDTOFactory, userDTOFactory, externalGroupDao, subscriberService, filterRepository, contextService);
//  }

}
