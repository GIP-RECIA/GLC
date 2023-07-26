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
package fr.recia.glc.ldap;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.recia.glc.ldap.dto.ICompositeKey;
import fr.recia.glc.ldap.enums.ContextType;
import fr.recia.glc.ldap.utils.CustomEnumSerializer;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author GIP RECIA - Julien Gribonvald 14 juin 2014
 */
@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Embeddable
public class ContextKey implements ICompositeKey<Long, ContextType>, Serializable {

  /** Serial Id. */
  private static final long serialVersionUID = -210670392798727508L;
  /** The identifier of the context. */
  @NonNull
  @NotNull
  @Basic
  @Column(name = "ctx_id", nullable = false)
  private Long keyId;

  /** The type of the context. */
  @NonNull
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = 25, name = "ctx_type", nullable = false)
  @JsonSerialize(using = CustomEnumSerializer.class)
  private ContextType keyType;

}
