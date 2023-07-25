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
package fr.recia.glc.ldap.enums;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * @author GIP RECIA - Julien Gribonvald 16 juin 2014
 */
@Converter(autoApply = true)
public class SubjectTypeConverter implements
  AttributeConverter<SubjectType, Integer> {

  @Override
  public Integer convertToDatabaseColumn(SubjectType attribute) {
    return attribute.getId();
  }

  @Override
  public SubjectType convertToEntityAttribute(Integer dbData) {
    return SubjectType.valueOf(dbData);
  }

}
