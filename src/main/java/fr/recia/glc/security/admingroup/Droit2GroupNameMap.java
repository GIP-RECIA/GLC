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
package fr.recia.glc.security.admingroup;

import org.esco.admingroup.group.IStem;
import org.esco.admingroup.parameters.ParameterKey;
import org.esco.admingroup.parameters.ParametersMap;

public class Droit2GroupNameMap extends Droit2SetMap<String> {

  /**
   * Associe le nom du groupe aux droits définis dans les paramètres du groupe.
   *
   * @param group
   * @return
   */
  public boolean addGroup(final IStem group) {
    if (group != null) {
      ParametersMap parameterMap = group.getParameters();
      if (parameterMap == null) return false;
      boolean modif = false;

      for (ParameterKey key : parameterMap.keySet()) {
        DroitApplicatif droit = DroitApplicatif.valueOf(key);
        if (droit != null) {
          modif |= add(droit, group.getName());
        }
      }
      return modif;
    }

    return false;
  }

}
