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

import fr.recia.glc.ldap.StructureFromGroup;
import fr.recia.glc.security.GLCRole;
import fr.recia.glc.security.GLCUser;
import fr.recia.glc.services.structure.StructureLoader;
import lombok.extern.slf4j.Slf4j;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Assertion;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final GLCProperties glcProperties;
    private final StructureLoader structureLoader;

    public SecurityConfiguration(GLCProperties glcProperties, StructureLoader structureLoader) {
        this.glcProperties = glcProperties;
        this.structureLoader = structureLoader;
    }

    /**
     * Propriétés du service CAS, avec notamment serviceID associé côté CAS
     */
    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(glcProperties.getCas().getCasServiceId());
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    /**
     * Redirection vers le CAS sur une route protégée
     */
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint(ServiceProperties serviceProperties) {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(glcProperties.getCas().getCasServerUrl()+"/login");
        entryPoint.setServiceProperties(serviceProperties);
        return entryPoint;
    }

    /**
     * Filtre qui intercepte la requête vers le service depuis le CAS (en passant par le navigateur) avec le ticket dans l'URL
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(AuthenticationManager authenticationManager) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setAuthenticationManager(authenticationManager);
        filter.setFilterProcessesUrl(glcProperties.getCas().getCasTicketCallback());
        return filter;
    }

    /**
     * Fournisseur d'authentitication qui va faire la requête au CAS pour valider le ticket reçu
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(ServiceProperties serviceProperties) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(serviceProperties);
        provider.setTicketValidator(new Cas30ServiceTicketValidator(glcProperties.getCas().getCasServerUrl()));
        provider.setAuthenticationUserDetailsService(customUserDetailsService());
        provider.setKey(glcProperties.getCas().getCasProviderKey());
        return provider;
    }

    /**
     * Transformer l’utilisateur CAS en utilisateur Spring Security pour remplir les User Details
     */
    @Bean
    public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> customUserDetailsService() {
        return (CasAssertionAuthenticationToken token) -> {
            Assertion assertion = token.getAssertion();
            Map<String, Object> attributes = assertion.getPrincipal().getAttributes();
            List<String> groups = (List<String>) attributes.get(glcProperties.getAdmin().getGroupsAttribute());
            String username = assertion.getPrincipal().getName();
            Pattern patternAdminLocal = Pattern.compile(glcProperties.getAdmin().getLocal());
            Pattern patternAdminSarapisLocal = Pattern.compile(glcProperties.getAdmin().getSarapisLocal());
            Pattern patternAdminCentral = Pattern.compile(glcProperties.getAdmin().getCentral());
            Pattern patternAdminCentralColl = Pattern.compile(glcProperties.getAdmin().getCentralColl());
            // Calcul dynamique des authorities en fonction des groupes
            // TODO : rôle dans le nom du groupe
            Map<GLCRole, Set<String>> rightsForEtabs = new HashMap<>();
            rightsForEtabs.put(GLCRole.WRITE, new HashSet<>());
            rightsForEtabs.put(GLCRole.READ, new HashSet<>());
            Set<GLCRole> rightsForColl = new HashSet<>();
            for(String group : groups){
                Matcher matcherAdminLocal = patternAdminLocal.matcher(group);
                Matcher matcherAdminSarapisLocal = patternAdminSarapisLocal.matcher(group);
                Matcher matcherAdminCentral = patternAdminCentral.matcher(group);
                Matcher matcherAdminCentralColl = patternAdminCentralColl.matcher(group);
                // Droits sur les établissements
                if(matcherAdminLocal.matches()){
                    final String uai = matcherAdminLocal.group(2);
                    rightsForEtabs.get(GLCRole.WRITE).add(uai);
                    rightsForEtabs.get(GLCRole.READ).add(uai);
                }
                if(matcherAdminSarapisLocal.matches()){
                    final String uai = matcherAdminSarapisLocal.group(2);
                    rightsForEtabs.get(GLCRole.WRITE).add(uai);
                    rightsForEtabs.get(GLCRole.READ).add(uai);
                }
                // Droits sur les branches
                if(matcherAdminCentral.matches()){
                    for(StructureFromGroup structureFromGroup : structureLoader.getStructuresOfBranch(matcherAdminCentral.group(1))){
                        rightsForEtabs.get(GLCRole.WRITE).add(structureFromGroup.getUAI());
                        rightsForEtabs.get(GLCRole.READ).add(structureFromGroup.getUAI());
                    }
                }
                if(matcherAdminCentralColl.matches()){
                    rightsForColl.add(GLCRole.WRITE);
                    rightsForColl.add(GLCRole.READ);
                }
            }
            return new GLCUser(username, "", new ArrayList<>(), rightsForEtabs, rightsForColl);
        };
    }

    /**
     * Transfert la demande d'auth depuis le filter vers le provider
     */
    @Bean
    public AuthenticationManager authenticationManager(CasAuthenticationProvider casAuthenticationProvider) {
        return new ProviderManager(casAuthenticationProvider);
    }

    /**
     * Gérer les requêtes de SLO qui arrivent du CAS pour détruire la session applicative
     */
    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        return new SingleSignOutFilter();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
        http
                // Filtre pour le logout à mettre avant tout
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
                // La partie exceptionHandling permet de rediriger sur le CAS si on a un 403
                .exceptionHandling(e -> e.authenticationEntryPoint(casAuthenticationEntryPoint(serviceProperties())))
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests
                                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .antMatchers("/health-check", "/api/config").permitAll()
                                .antMatchers("/ui/**", "/").authenticated()
                                .antMatchers("/api/**").authenticated()
                                // Cet endpoint doit être accessible car c'est le callback du CAS vers l'appli spring pour faire valider le ticket
                                .antMatchers(glcProperties.getCas().getCasTicketCallback()).permitAll()
                                .anyRequest().denyAll());
        return http.build();
    }

}
