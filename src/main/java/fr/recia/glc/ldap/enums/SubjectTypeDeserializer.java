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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;

import java.io.IOException;

/**
 * Created by jgribonvald on 01/04/15.
 */
public class SubjectTypeDeserializer extends JsonDeserializer<SubjectType> {

  @Override
  public SubjectType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    SubjectType type = SubjectType.fromName(jp.getValueAsString());
    if (type != null) return type;
    throw new JsonMappingException(jp, String.format("Invalid value '%s' for %s, must be in range of %s",
      jp.getValueAsString(), SubjectType.class.getSimpleName(), SubjectType.values().toString()
    ));
  }

}
