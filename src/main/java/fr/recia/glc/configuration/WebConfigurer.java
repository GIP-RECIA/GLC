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

import fr.recia.glc.web.filter.StaticResourcesProductionFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.server.MimeMappings;
import org.springframework.boot.web.server.WebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.http.MediaType;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.EnumSet;

;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Slf4j
@Configuration
public class WebConfigurer implements ServletContextInitializer, WebServerFactoryCustomizer<WebServerFactory> {

  @Inject
  private Environment env;

  @Override
  public void onStartup(ServletContext servletContext) throws ServletException {
    log.info("Web application configuration, using profiles: {}", Arrays.toString(env.getActiveProfiles()));
    EnumSet<DispatcherType> disps = EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ASYNC);
    if (env.acceptsProfiles(Profiles.of(Constants.SPRING_PROFILE_PRODUCTION))) {
//      initCachingHttpHeadersFilter(servletContext, disps);
      initStaticResourcesProductionFilter(servletContext, disps);
//      initGzipFilter(servletContext, disps);
    }
    log.info("Web application fully configured");
  }

  /**
   * Set up Mime types.
   */
  @Override
  public void customize(WebServerFactory server) {
    if (server instanceof ConfigurableServletWebServerFactory) {
      MimeMappings mappings = new MimeMappings(MimeMappings.DEFAULT);
      // IE issue, see https://github.com/jhipster/generator-jhipster/pull/711
      mappings.add("html", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
      // CloudFoundry issue, see https://github.com/cloudfoundry/gorouter/issues/64
      mappings.add("json", MediaType.TEXT_HTML_VALUE + ";charset=" + StandardCharsets.UTF_8.name().toLowerCase());
      ConfigurableServletWebServerFactory servletWebServer = (ConfigurableServletWebServerFactory) server;
      servletWebServer.setMimeMappings(mappings);
    }
  }

//  /**
//   * Initializes the GZip filter.
//   */
//  private void initGzipFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
//    log.debug("Registering GZip Filter");
//    FilterRegistration.Dynamic compressingFilter = servletContext.addFilter("gzipFilter", new GZipServletFilter());
//    Map<String, String> parameters = new HashMap<>();
//    compressingFilter.setInitParameters(parameters);
//    compressingFilter.addMappingForUrlPatterns(disps, true, "*.css");
//    compressingFilter.addMappingForUrlPatterns(disps, true, "*.json");
//    compressingFilter.addMappingForUrlPatterns(disps, true, "*.html");
//    compressingFilter.addMappingForUrlPatterns(disps, true, "*.js");
//    compressingFilter.addMappingForUrlPatterns(disps, true, "*.svg");
//    compressingFilter.addMappingForUrlPatterns(disps, true, "*.ttf");
//    compressingFilter.addMappingForUrlPatterns(disps, true, "*.woff2");
//    compressingFilter.addMappingForUrlPatterns(disps, true, "/api/*");
//    compressingFilter.addMappingForUrlPatterns(disps, true, "/management/*");
//    compressingFilter.setAsyncSupported(true);
//  }

  /**
   * Initializes the static resources production Filter.
   */
  private void initStaticResourcesProductionFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {

    log.debug("Registering static resources production Filter");
    FilterRegistration.Dynamic staticResourcesProductionFilter = servletContext.addFilter("staticResourcesProductionFilter", new StaticResourcesProductionFilter());

    staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/");
    staticResourcesProductionFilter.addMappingForUrlPatterns(disps, true, "/ui/*");
    staticResourcesProductionFilter.setAsyncSupported(true);
  }

//  /**
//   * Initializes the cachig HTTP Headers Filter.
//   */
//  private void initCachingHttpHeadersFilter(ServletContext servletContext, EnumSet<DispatcherType> disps) {
//    log.debug("Registering Caching HTTP Headers Filter");
//    FilterRegistration.Dynamic cachingHttpHeadersFilter = servletContext.addFilter("cachingHttpHeadersFilter", new CachingHttpHeadersFilter());
//
//    cachingHttpHeadersFilter.addMappingForUrlPatterns(disps, true, "/ui/assets/*");
//    cachingHttpHeadersFilter.setAsyncSupported(true);
//  }

}
