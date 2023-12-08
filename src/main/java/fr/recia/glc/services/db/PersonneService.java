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

import fr.recia.glc.db.dto.personne.PersonneDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.APersonneAStructure;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.repositories.APersonneAStructureRepository;
import fr.recia.glc.db.repositories.personne.APersonneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
public class PersonneService {

  @Autowired
  private APersonneAStructureRepository<APersonneAStructure> aPersonneAStructureRepository;
  @Autowired
  private APersonneRepository<APersonne> aPersonneRepository;

  public List<SimplePersonneDto> searchPersoonne(String name) {
    return aPersonneRepository.findByNameLike(name);
  }

  public List<SimplePersonneDto> getPersonnes(Long structureId) {
    final List<Long> personnesIds = aPersonneAStructureRepository.findPersonneByStructureId(structureId);
    return aPersonneRepository.findByPersonneIds(new HashSet<>(personnesIds));
  }

  public PersonneDto getPersonne(Long id) {
    return aPersonneRepository.findByPersonneId(id);
  }

  public SimplePersonneDto getPersonneSimple(Long id) {
    return aPersonneRepository.findByPersonneIdSimple(id);
  }

}
