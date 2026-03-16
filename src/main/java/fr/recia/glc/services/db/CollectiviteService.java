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
package fr.recia.glc.services.db;

import fr.recia.glc.db.dto.structure.SimpleStructureDto;
import fr.recia.glc.db.entities.structure.CollectiviteLocale;
import fr.recia.glc.db.repositories.structure.CollectiviteLocaleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class CollectiviteService {

  @Autowired
  private CollectiviteLocaleRepository<CollectiviteLocale> collectiviteLocaleRepository;

  @Cacheable(value = "collectivites")
  public List<SimpleStructureDto> getCollectivites() {
    log.trace("getCollectivites");
    List<CollectiviteLocale> collectiviteLocales = collectiviteLocaleRepository.findAll();
    List<SimpleStructureDto> simpleStructureDtos = new ArrayList<>();
    for(CollectiviteLocale collectiviteLocale : collectiviteLocales){
      simpleStructureDtos.add(new SimpleStructureDto(collectiviteLocale));
    }
    return simpleStructureDtos;
  }

}
