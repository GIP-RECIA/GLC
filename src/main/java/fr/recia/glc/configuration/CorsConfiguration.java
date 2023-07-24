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
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@Slf4j
public class CorsConfiguration {

  @Value("${security-configuration.cors.enable}")
  private boolean corsEnable;
  @Value("${security-configuration.cors.allow-credentials}")
  private Boolean corsAllowCredentials;
  @Value("${security-configuration.cors.allowed-origins}")
  private List<String> corsAllowedOrigins;
  @Value("${security-configuration.cors.exposed-headers}")
  private List<String> corsExposedHeaders;
  @Value("${security-configuration.cors.allowed-headers}")
  private List<String> corsAllowedHeaders;
  @Value("${security-configuration.cors.allowed-methods}")
  private List<String> corsAllowedMethods;

  @Bean
  CorsConfigurationSource corsConfigurationSource() {
    final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

    if (log.isWarnEnabled()) log.warn("CORS: {}", corsEnable);
    if (corsEnable) {
      org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

      configuration.setAllowCredentials(corsAllowCredentials);
      configuration.setAllowedOrigins(corsAllowedOrigins);
      configuration.setExposedHeaders(corsExposedHeaders);
      configuration.setAllowedHeaders(corsAllowedHeaders);
      configuration.setAllowedMethods(corsAllowedMethods);

      source.registerCorsConfiguration("/**", configuration);
    }

    return source;
  }

}
