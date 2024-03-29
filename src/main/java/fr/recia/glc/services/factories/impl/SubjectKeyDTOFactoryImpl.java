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
package fr.recia.glc.services.factories.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import fr.recia.glc.ldap.SubjectKey;
import fr.recia.glc.ldap.enums.SubjectType;
import fr.recia.glc.services.factories.CompositeKeyDTOFactory;
import fr.recia.glc.web.dto.SubjectKeyDTO;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

@Service
public class SubjectKeyDTOFactoryImpl implements
  CompositeKeyDTOFactory<SubjectKeyDTO, SubjectKey, String, SubjectType> {

  public SubjectKey convertToModelKey(@NotNull final SubjectKeyDTO id) {
    return new SubjectKey(id.getKeyId(), id.getKeyType());
  }

  public SubjectKeyDTO convertToDTOKey(@NotNull final SubjectKey id) {
    return new SubjectKeyDTO(id.getKeyId(), id.getKeyType());
  }

  public Set<SubjectKey> convertToModelKey(@NotNull final Set<SubjectKeyDTO> dtos) {
    final Set<SubjectKey> models = Sets.newHashSet();
    for (SubjectKeyDTO dto : dtos) {
      models.add(this.convertToModelKey(dto));
    }
    return models;
  }

  public List<SubjectKey> convertToModelKey(@NotNull final List<SubjectKeyDTO> dtos) {
    final List<SubjectKey> models = Lists.newArrayList();
    for (SubjectKeyDTO dto : dtos) {
      models.add(this.convertToModelKey(dto));
    }
    return models;
  }

  public Set<SubjectKeyDTO> convertToDTOKey(@NotNull final Set<SubjectKey> models) {
    final Set<SubjectKeyDTO> dtos = Sets.newHashSet();
    for (SubjectKey model : models) {
      dtos.add(this.convertToDTOKey(model));
    }
    return dtos;
  }

  public List<SubjectKeyDTO> convertToDTOKey(@NotNull final List<SubjectKey> models) {
    final List<SubjectKeyDTO> dtos = Lists.newArrayList();
    for (SubjectKey model : models) {
      dtos.add(this.convertToDTOKey(model));
    }
    return dtos;
  }

}
