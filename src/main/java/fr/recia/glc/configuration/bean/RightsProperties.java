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
package fr.recia.glc.configuration.bean;

import javax.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
public class RightsProperties {
    private Map<String, Map<String, RoleProperties>> services;
    private List<GroupProperties> declaredGroups;
    private Map<String, String> declaredGroupsMap;

    @PostConstruct
    public void init() {
        declaredGroupsMap = declaredGroups.stream()
                .collect(Collectors.toUnmodifiableMap(
                        GroupProperties::getGrouperPath,
                        GroupProperties::getDisplayName
                ));
    }
}
