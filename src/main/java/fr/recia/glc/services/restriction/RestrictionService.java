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
package fr.recia.glc.services.restriction;

import fr.recia.glc.configuration.GLCProperties;
import fr.recia.glc.web.dto.restriction.RestrictionEtab;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class RestrictionService {

    private final RestTemplate restTemplate;
    private final GLCProperties glcProperties;

    public RestrictionService(RestTemplate restTemplateRestriction, GLCProperties glcProperties) {
        this.restTemplate = restTemplateRestriction;
        this.glcProperties = glcProperties;
    }

    public RestrictionEtab getRestrictions(String uai) {
        return restTemplate.exchange(glcProperties.getRestrictionRentree().getUrl() + "/" + uai, HttpMethod.GET, null, RestrictionEtab.class).getBody();
    }

    public void setNewRestriction(String uai, RestrictionEtab restrictionEtab) {
        HttpEntity<?> entity = new HttpEntity<>(restrictionEtab);
        restTemplate.exchange(glcProperties.getRestrictionRentree().getUrl() + "/" + uai, HttpMethod.POST, entity, RestrictionEtab.class);
    }

}
