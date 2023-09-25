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
package fr.recia.glc.web.dto;

import fr.recia.glc.ldap.enums.SubjectType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@Getter
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class SubjectDTO extends AbstractIdDTO<SubjectKeyDTO> implements IAbstractDTO<SubjectKeyDTO>, Serializable {

  private String displayName;

  private boolean foundOnExternalSource;

  public SubjectDTO(@NotNull final SubjectKeyDTO modelId, final String displayName, final boolean foundOnExternalSource) {
    super(modelId);
    this.displayName = displayName;
    this.foundOnExternalSource = foundOnExternalSource;
  }

  public SubjectDTO(@NotNull final String keyId, @NotNull final SubjectType keyType, final String displayName, final boolean foundOnExternalSource) {
    super(new SubjectKeyDTO(keyId, keyType));
    this.displayName = displayName;
    this.foundOnExternalSource = foundOnExternalSource;
  }

  public SubjectDTO(@NotNull final SubjectKeyDTO modelId) {
    super(modelId);
  }

  public SubjectDTO(@NotNull final String keyId, @NotNull final SubjectType keyType) {
    super(new SubjectKeyDTO(keyId, keyType));
  }

}
