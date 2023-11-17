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
import fr.recia.glc.security.AuthoritiesConstants;
import fr.recia.glc.security.CustomSessionFixationProtectionStrategy;
import fr.recia.glc.security.cas.AjaxAuthenticationFailureHandler;
import fr.recia.glc.security.cas.AjaxAuthenticationSuccessHandler;
import fr.recia.glc.security.cas.AjaxLogoutSuccessHandler;
import fr.recia.glc.security.cas.CustomSingleSignOutFilter;
import fr.recia.glc.security.cas.RememberCasAuthenticationEntryPoint;
import fr.recia.glc.security.cas.RememberCasAuthenticationProvider;
import fr.recia.glc.security.cas.RememberWebAuthenticationDetailsSource;
import fr.recia.glc.services.beans.ServiceUrlHelper;
import fr.recia.glc.web.filter.CsrfCookieGeneratorFilter;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionStrategy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.util.Assert;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

  public static final String PROTECTED_PATH = "/protected/";
  private static final String APP_CONTEXT_PATH = "server.servlet.context-path";
  private static final String APP_URI_LOGIN = "/app/login";

  private final Environment env;
  private final GLCProperties glcProperties;

  @Inject
  private AjaxAuthenticationSuccessHandler ajaxAuthenticationSuccessHandler;
  @Inject
  private AjaxAuthenticationFailureHandler ajaxAuthenticationFailureHandler;
  @Inject
  private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;
  @Inject
  private AuthenticationUserDetailsService<CasAssertionAuthenticationToken> userDetailsService;

  public SecurityConfiguration(Environment env, GLCProperties glcProperties) {
    this.env = env;
    this.glcProperties = glcProperties;
  }

  // CAS

  @Bean
  public ServiceProperties serviceProperties() {
    ServiceProperties sp = new ServiceProperties();
    Assert.hasText(glcProperties.getSecurity().getAuthUriFilterPath(), "The CAS URI service should be set to be able to apply CAS Auth Filter, see property 'app.security.auth-uri-filter-path'");
    sp.setService(glcProperties.getSecurity().getAuthUriFilterPath());
    sp.setSendRenew(false);
    sp.setAuthenticateAllArtifacts(true);

    return sp;
  }

  @Bean
  public ServiceUrlHelper serviceUrlHelper() {
    String ctxPath = env.getRequiredProperty(APP_CONTEXT_PATH);
    if (!ctxPath.startsWith("/")) ctxPath = "/" + ctxPath;
    final String protocol = glcProperties.getSecurity().getProtocol();
    Assert.isTrue(Lists.newArrayList("http://", "https://").contains(protocol), "Protocol param doesn't match required value, see property 'app.security.protocol'");
    final List<String> domainNames = Lists.newArrayList(glcProperties.getSecurity().getAuthorizedDomainNames());
    Assert.notEmpty(domainNames, "The list of the application Domain Names set shouldn't be empty, see property 'app.security.authorizedDomainNames'");
    ServiceUrlHelper serviceUrlHelper = new ServiceUrlHelper(ctxPath, domainNames, protocol, "/view/item/");
    log.info("ServiceUrlHelper is configured with properties : {}", serviceUrlHelper);

    return serviceUrlHelper;
  }

  @Bean
  String getCasTargetUrlParameter() {
    Assert.hasText(glcProperties.getSecurity().getRedirectParamName(), "Redirect Param Name shouldn't be null, see property 'app.security.redirectParamName'");
    return glcProperties.getSecurity().getRedirectParamName();
  }

  @Bean
  protected AuthenticationManager authenticationManager() {
    return new ProviderManager(Collections.singletonList(casAuthenticationProvider()));
  }

  @Bean
  public SessionAuthenticationStrategy sessionStrategy() {
    SessionFixationProtectionStrategy sessionStrategy = new CustomSessionFixationProtectionStrategy(
      serviceUrlHelper(), serviceProperties(), getCasTargetUrlParameter()
    );
    sessionStrategy.setMigrateSessionAttributes(false);

    return sessionStrategy;
  }

  @Bean
  public SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler() {
    SimpleUrlAuthenticationSuccessHandler authenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler();
    authenticationSuccessHandler.setDefaultTargetUrl("/");
    authenticationSuccessHandler.setTargetUrlParameter(getCasTargetUrlParameter());

    return authenticationSuccessHandler;
  }

  @Bean
  public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
    return new Cas20ServiceTicketValidator(glcProperties.getCas().getUrlPrefix());
  }

  @Bean
  @DependsOn({"customUserDetailsService"})
  public RememberCasAuthenticationProvider casAuthenticationProvider() {
    RememberCasAuthenticationProvider casAuthenticationProvider = new RememberCasAuthenticationProvider();
    casAuthenticationProvider.setAuthenticationUserDetailsService(userDetailsService);
    casAuthenticationProvider.setServiceProperties(serviceProperties());
    casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
    Assert.hasText(glcProperties.getSecurity().getIdKeyProvider(), "The CAS security Key should be set, see property 'app.security.idKeyProvider'");
    casAuthenticationProvider.setKey(glcProperties.getSecurity().getIdKeyProvider());

    return casAuthenticationProvider;
  }

  @Bean
  public RememberCasAuthenticationEntryPoint casAuthenticationEntryPoint() {
    RememberCasAuthenticationEntryPoint casAuthenticationEntryPoint = new RememberCasAuthenticationEntryPoint();
    casAuthenticationEntryPoint.setLoginUrl(glcProperties.getCas().getUrlLogin());
    casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
    casAuthenticationEntryPoint.setUrlHelper(serviceUrlHelper());
    casAuthenticationEntryPoint.setPathLogin(APP_URI_LOGIN);

    return casAuthenticationEntryPoint;
  }

  @Bean
  public CasAuthenticationFilter casAuthenticationFilter() {
    CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
    Assert.hasText(glcProperties.getSecurity().getAuthUriFilterPath(), "The CAS URI service should be set to be able to apply CAS Auth Filter, see property 'app.security.auth-uri-filter-path'");
    casAuthenticationFilter.setFilterProcessesUrl("/" + glcProperties.getSecurity().getAuthUriFilterPath());
    casAuthenticationFilter.setAuthenticationManager(authenticationManager());
    casAuthenticationFilter.setAuthenticationDetailsSource(new RememberWebAuthenticationDetailsSource(
      serviceUrlHelper(), serviceProperties(), getCasTargetUrlParameter()
    ));
    casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy());
    casAuthenticationFilter.setAuthenticationFailureHandler(ajaxAuthenticationFailureHandler);
    casAuthenticationFilter.setAuthenticationSuccessHandler(ajaxAuthenticationSuccessHandler);

    return casAuthenticationFilter;
  }

  @Bean
  public CustomSingleSignOutFilter singleSignOutFilter() {
    CustomSingleSignOutFilter singleSignOutFilter = new CustomSingleSignOutFilter();
    singleSignOutFilter.setCasServerUrlPrefix(glcProperties.getCas().getUrlPrefix());

    return singleSignOutFilter;
  }

  // Spring

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class).exceptionHandling()
      .authenticationEntryPoint(casAuthenticationEntryPoint());

    http
      .addFilterBefore(casAuthenticationFilter(), BasicAuthenticationFilter.class)
      .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);

    http.headers().frameOptions().disable();

    http
      .authorizeHttpRequests(authz -> authz
        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        .antMatchers("/app/**/*.{js,html}", "/ui/**").permitAll()
        .antMatchers("/health-check", "/api/config").permitAll()
        .antMatchers("/api/**").hasAuthority(AuthoritiesConstants.USER)
        .antMatchers(
          "/app/**",
          PROTECTED_PATH + "**"
        ).authenticated()
        .anyRequest().denyAll()
      );

    http
      .logout()
      .logoutUrl("/api/logout")
      .logoutSuccessHandler(ajaxLogoutSuccessHandler)
      .invalidateHttpSession(true)
      .deleteCookies("JSESSIONID")
      .permitAll();

    return http.build();
  }

}
