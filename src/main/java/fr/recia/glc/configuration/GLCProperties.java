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

import com.fasterxml.jackson.core.JsonProcessingException;
import fr.recia.glc.configuration.bean.AlertProperties;
import fr.recia.glc.configuration.bean.CASProperties;
import fr.recia.glc.configuration.bean.CorsProperties;
import fr.recia.glc.configuration.bean.CustomConfigProperties;
import fr.recia.glc.configuration.bean.CustomLdapProperties;
import fr.recia.glc.configuration.bean.CustomMailProperties;
import fr.recia.glc.configuration.bean.CustomMetricsProperties;
import fr.recia.glc.configuration.bean.FrontProperties;
import fr.recia.glc.configuration.bean.IpRangeProperties;
import fr.recia.glc.configuration.bean.RoleMappingProperties;
import fr.recia.glc.configuration.bean.SecurityProperties;
import fr.recia.glc.utils.ListUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
@ConfigurationProperties(
  prefix = "app",
  ignoreUnknownFields = false
)
@Data
@Validated
@Slf4j
public class GLCProperties {

  private CASProperties cas;
  private CorsProperties cors;
  private CustomConfigProperties customConfig;
  private SecurityProperties security;
  private FrontProperties front;
  private List<AlertProperties> alerts;

  private RoleMappingProperties admins;
  private RoleMappingProperties users;
  private CustomMailProperties mail;
  private IpRangeProperties authorizedServices;
  private CustomMetricsProperties metrics;
  private CustomLdapProperties ldap;

  @PostConstruct
  private void init() throws JsonProcessingException {
    log.info("Loaded properties: {}", this);
  }

  @Override
  public String toString() {
    return "{\n" +
      "\"cas\" : " + cas + "," +
      "\n\"cors\" : " + cors + "," +
      "\n\"customConfig\" : " + customConfig + "," +
      "\n\"security\" : " + security + "," +
      "\n\"front\" : " + front + "," +
      "\n\"alerts\" : " + ListUtils.toStringList(alerts, ",\n", "[\n", "\n]") + "," +
      "\n\"admins\" : " + admins + "," +
      "\n\"users\" : " + users + "," +
      "\n\"mail\" : " + mail + "," +
      "\n\"authorizedServices\" : " + authorizedServices + "," +
      "\n\"metrics\" : " + metrics + "," +
      "\n\"ldap\" : " + ldap +
      "\n}";
  }

}
