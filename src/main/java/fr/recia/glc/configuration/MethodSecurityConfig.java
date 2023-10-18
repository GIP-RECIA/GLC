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

import fr.recia.glc.security.AuthoritiesConstants;
import fr.recia.glc.security.CustomPermissionEvaluator;
import fr.recia.glc.services.beans.AuthoritiesDefinition;
import fr.recia.glc.services.beans.IAuthoritiesDefinition;
import fr.recia.glc.services.evaluators.IEvaluation;
import fr.recia.glc.services.evaluators.OperatorEvaluation;
import fr.recia.glc.services.evaluators.OperatorType;
import fr.recia.glc.services.evaluators.StringEvaluationMode;
import fr.recia.glc.services.evaluators.UserAttributesEvaluation;
import fr.recia.glc.services.evaluators.UserMultivaluedAttributesEvaluation;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.vote.RoleHierarchyVoter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.util.Assert;

import java.util.HashSet;
import java.util.Set;

@Configuration
@AutoConfigureBefore(SecurityConfiguration.class)
@EnableGlobalMethodSecurity(prePostEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig extends GlobalMethodSecurityConfiguration {

  private ApplicationContext applicationContext;
  private final GLCProperties glcProperties;

  public MethodSecurityConfig(ApplicationContext applicationContext, GLCProperties glcProperties) {
    this.applicationContext = applicationContext;
    this.glcProperties = glcProperties;
  }

  @Bean
  public RoleHierarchyVoter roleVoter() {
    return new RoleHierarchyVoter(roleHierarchy());
  }

  @Bean
  public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl rhi = new RoleHierarchyImpl();
    rhi.setHierarchy(
      AuthoritiesConstants.ADMIN + " > " +
        AuthoritiesConstants.USER + " > " +
        AuthoritiesConstants.AUTHENTICATED + " > " +
        AuthoritiesConstants.ANONYMOUS
    );

    return rhi;
  }

  @Bean
  public PermissionEvaluator permissionEvaluator() {
    return new CustomPermissionEvaluator();
  }

  @Override
  protected MethodSecurityExpressionHandler createExpressionHandler() {
    DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
    expressionHandler.setPermissionEvaluator(permissionEvaluator());
    expressionHandler.setRoleHierarchy(roleHierarchy());
    expressionHandler.setApplicationContext(applicationContext);

    return expressionHandler;
  }

  @Bean
  public DefaultWebSecurityExpressionHandler webExpressionHandler() {
    DefaultWebSecurityExpressionHandler expressionHandler = new DefaultWebSecurityExpressionHandler();
    expressionHandler.setRoleHierarchy(roleHierarchy());
    expressionHandler.setPermissionEvaluator(permissionEvaluator());
    expressionHandler.setApplicationContext(applicationContext);

    return expressionHandler;
  }

  @Bean
  public IAuthoritiesDefinition mainRolesDefs() {
    Assert.notNull(glcProperties, "Properties must not be null");
    AuthoritiesDefinition defs = new AuthoritiesDefinition();

    Set<IEvaluation> set = new HashSet<>();

    final String userAdmin = glcProperties.getAdmins().getUserName();
    if (userAdmin != null && !userAdmin.isEmpty()) {
      final UserAttributesEvaluation uae1 =
        new UserAttributesEvaluation("uid", userAdmin, StringEvaluationMode.EQUALS);
      set.add(uae1);
    }

    final String groupAdmin = glcProperties.getAdmins().getGroupName();
    if (groupAdmin != null && !groupAdmin.isEmpty()) {
      final UserAttributesEvaluation uae2 =
        new UserMultivaluedAttributesEvaluation("isMemberOf", groupAdmin, StringEvaluationMode.EQUALS);
      set.add(uae2);
    }

    Assert.isTrue(
      !set.isEmpty(),
      "Properties that define admins aren't set in properties, there are needed to define an Admin user," +
        " name should be 'app.admins.userName' or 'app.admins.groupName'"
    );

    OperatorEvaluation admins = new OperatorEvaluation(OperatorType.OR, set);
    defs.setAdmins(admins);

    final String groupUsers = glcProperties.getUsers().getGroupName();
    UserMultivaluedAttributesEvaluation umae =
      new UserMultivaluedAttributesEvaluation("isMemberOf", groupUsers, StringEvaluationMode.MATCH);
    defs.setUsers(umae);

    Assert.isTrue(
      !groupUsers.isEmpty(),
      "Properties that define users aren't set in properties, there are needed to define all access users," +
        " name should be 'app.users.groupName'"
    );

    return defs;
  }


}
