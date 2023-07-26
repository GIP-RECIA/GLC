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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.ldap.repository.config.EnableLdapRepositories;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

import javax.naming.directory.SearchControls;

@Configuration
@EnableLdapRepositories(basePackages = "fr.recia.glc.ldap.repositories")
@Slf4j
public class LdapConfiguration {

  @Value("${spring.ldap.url}")
  private String ldapUrl;
  @Value("${spring.ldap.base}")
  private String ldapBase;
  @Value("${spring.ldap.username}")
  private String ldapUsername;
  @Value("${spring.ldap.password}")
  private String ldapPassword;
  @Value("${spring.ldap.template.count-limit}")
  private String ldapTemplateCountLimit;
  @Value("${spring.ldap.template.time-limit}")
  private String ldapTemplateTimeLimit;

  @Bean
  public LdapContextSource contextSource() {
    log.debug("Configuring LdapContextSource");
    final LdapContextSource ldapCtx = new LdapContextSource();
    ldapCtx.setAnonymousReadOnly(false);
    ldapCtx.setBase(ldapBase);
    ldapCtx.setUrl(ldapUrl);
    ldapCtx.setUserDn(ldapBase);
    ldapCtx.setPassword(ldapPassword);
    ldapCtx.setPooled(false);

    return ldapCtx;
  }

  @Bean
  public LdapTemplate ldapTemplate() {
    final LdapTemplate ldapTpl = new LdapTemplate();
    ldapTpl.setContextSource(contextSource());
    ldapTpl.setDefaultCountLimit(Integer.parseInt(ldapTemplateCountLimit));
    ldapTpl.setDefaultTimeLimit(Integer.parseInt(ldapTemplateTimeLimit));
    ldapTpl.setDefaultSearchScope(SearchControls.SUBTREE_SCOPE);
    ldapTpl.setIgnoreNameNotFoundException(false);
    ldapTpl.setIgnorePartialResultException(false);
    ldapTpl.setIgnoreSizeLimitExceededException(true);

    return ldapTpl;
  }

}
