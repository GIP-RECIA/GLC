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
package fr.recia.glc.services.factories;

import fr.recia.glc.db.entities.common.AbstractEntity;
import fr.recia.glc.web.dto.AbstractIdDTO;

import java.io.Serializable;

/**
 * Data Transfer Object factory for converting to and from models.
 *
 * @param <DTObject> DTO type for the corresponding model
 * @param <M>        Model type
 * @param <ID>       Type of id of Model type
 * @author GIP RECIA - Julien Gribonvald
 */
public interface DTOFactory<DTObject extends AbstractIdDTO<ID>, M extends AbstractEntity<ID>, ID extends Serializable>
  extends GenericDTOFactory<DTObject, M, ID, ID> {


}
