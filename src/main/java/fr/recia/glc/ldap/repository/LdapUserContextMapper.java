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

import com.google.common.collect.Lists;
import fr.recia.glc.ldap.ExternalUser;
import fr.recia.glc.ldap.ExternalUserHelper;
import fr.recia.glc.ldap.IExternalUser;
import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.util.Assert;

import javax.naming.NamingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author GIP RECIA - Julien Gribonvald 11 juil. 2014
 */
public class LdapUserContextMapper implements ContextMapper<IExternalUser> {

  private ExternalUserHelper externalUserHelper;

  /**
   * @param externalUserHelper
   */
  public LdapUserContextMapper(ExternalUserHelper externalUserHelper) {
    super();
    this.externalUserHelper = externalUserHelper;
  }

  @Override
  public IExternalUser mapFromContext(Object ctx) throws NamingException {
    Assert.notNull(externalUserHelper, "The externalUserHelper should not be null !");
    DirContextAdapter context = (DirContextAdapter) ctx;
    Map<String, List<String>> attrs = new HashMap<>();
    ExternalUser person = new ExternalUser();
    person.setId(context.getStringAttribute(externalUserHelper.getUserIdAttribute()));
    attrs.put(externalUserHelper.getUserIdAttribute(), Lists.newArrayList(person.getId()));
    person.setDisplayName(context.getStringAttribute(externalUserHelper.getUserDisplayNameAttribute()));
    attrs.put(externalUserHelper.getUserDisplayNameAttribute(), Lists.newArrayList(person.getDisplayName()));

    if (context.attributeExists(externalUserHelper.getUserEmailAttribute())) {
      person.setEmail(context.getStringAttribute(externalUserHelper.getUserEmailAttribute()));
      attrs.put(externalUserHelper.getUserEmailAttribute(), Lists.newArrayList(person.getEmail()));
    }

    if (externalUserHelper.getUserGroupAttribute() != null
      && !externalUserHelper.getUserGroupAttribute().isEmpty()
      && context.attributeExists(externalUserHelper.getUserGroupAttribute())) {
      attrs.put(
        externalUserHelper.getUserGroupAttribute(),
        Lists.newArrayList(context.getStringAttributes(externalUserHelper.getUserGroupAttribute()))
      );
    }

    if (externalUserHelper.getOtherUserDisplayedAttributes() != null
      && !externalUserHelper.getOtherUserDisplayedAttributes().isEmpty()) {
      for (String attr : externalUserHelper.getOtherUserDisplayedAttributes()) {
        if (context.attributeExists(attr)) attrs.put(attr, Arrays.asList(context.getStringAttributes(attr)));
      }
    }

    if (externalUserHelper.getOtherUserAttributes() != null && !externalUserHelper.getOtherUserAttributes().isEmpty()) {
      for (String attr : externalUserHelper.getOtherUserAttributes()) {
        if (context.attributeExists(attr)) attrs.put(attr, Arrays.asList(context.getStringAttributes(attr)));
      }
    }
    person.setAttributes(attrs);

    return person;
  }

  public ExternalUserHelper getLdapUserHelper() {
    return externalUserHelper;
  }

  public void setLdapUserHelper(ExternalUserHelper externalUserHelper) {
    this.externalUserHelper = externalUserHelper;
  }

}
