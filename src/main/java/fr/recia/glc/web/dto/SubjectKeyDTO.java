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

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.recia.glc.ldap.enums.SubjectType;
import fr.recia.glc.services.utils.CustomEnumSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald
 * 15 oct. 2014
 */
@Data
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class SubjectKeyDTO implements ICompositeKey<String, SubjectType>, Serializable {

  @NotNull
  @NonNull
  private String keyId;
  @NotNull
  @NonNull
  @JsonSerialize(using = CustomEnumSerializer.class)
  private SubjectType keyType;

  /**
   * @param keyId
   * @param keyType
   */
  public SubjectKeyDTO(@NonNull final String keyId, @NonNull final SubjectType keyType) {
    this.keyId = keyId;
    this.keyType = keyType;
  }

}
