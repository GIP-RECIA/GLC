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
package fr.recia.glc.services.beans;

import com.google.common.collect.Sets;
import fr.recia.glc.ldap.IStructure;
import fr.recia.glc.ldap.repository.IExternalGroupDao;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@Slf4j
public class StructureLoaderImpl implements IStructureLoader {

  Set<IStructure> loadedStructures = new HashSet<>();

  Map<String, Set<IStructure>> loadedStructuresByBranch = new HashMap<>();

  @Setter
  private IExternalGroupDao groupDao;

  public StructureLoaderImpl(IExternalGroupDao groupDao) {
    this.groupDao = groupDao;
  }

  @PostConstruct
  private void loadingStructures() {
    loadedStructures = groupDao.getStructuresFromGroups();
    loadedStructures.forEach(structure -> {
      if (loadedStructuresByBranch.containsKey(structure.getGroupBranch()))
        loadedStructuresByBranch.get(structure.getGroupBranch()).add(structure);
      else loadedStructuresByBranch.put(structure.getGroupBranch(), Sets.newHashSet(structure));
    });
    log.debug(
      "Loaded Structure: {}",
      loadedStructuresByBranch.keySet().stream()
        .map(key -> {
          List<String> values = loadedStructuresByBranch.get(key).stream()
            .map(structure -> "\n\t\t\t{" +
              "\n\t\t\t\t\"keyId\": \"" + structure.getStructureKey().getKeyId() + "\"" +
              ",\n\t\t\t\t\"keyType\": \"" + structure.getStructureKey().getKeyType() + "\"" +
              ",\n\t\t\t\t\"UAI\": \"" + structure.getUAI() + "\"" +
              ",\n\t\t\t\t\"displayName\": \"" + structure.getDisplayName() + "\"" +
              ",\n\t\t\t\t\"groupBranch\": \"" + structure.getGroupBranch() + "\"" +
              ",\n\t\t\t\t\"groupNameEtab\": \"" + structure.getGroupNameEtab() + "\"" +
              "\n\t\t\t}"
            )
            .collect(Collectors.toList());

          return "\t{" +
            "\n\t\t\"" + key + "\": " + values +
            "\n\t}";
        })
        .collect(Collectors.joining(",\n", "[\n", "\n]"))
    );
    log.debug(
      "Loaded Structure recap: {}",
      loadedStructuresByBranch.keySet().stream()
        .map(key -> "\t{ \"" + key + "\": " + loadedStructuresByBranch.get(key).size() + " }")
        .collect(Collectors.joining(",\n", "[\n", "\n]"))
    );
  }

  @Override
  public Set<IStructure> getStructuresOfBranch(String branchGroup) {
    return loadedStructuresByBranch.getOrDefault(branchGroup, Collections.emptySet());
  }

  @Override
  public Set<IStructure> getAllStructures() {
    return loadedStructures;
  }

}
