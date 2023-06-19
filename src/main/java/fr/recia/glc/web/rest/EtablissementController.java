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
package fr.recia.glc.web.rest;

import fr.recia.glc.db.dto.structure.EtablissementDto;
import fr.recia.glc.db.entities.fonction.Fonction;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.repositories.fonction.FonctionRepository;
import fr.recia.glc.db.repositories.structure.EtablissementRepository;
import fr.recia.glc.models.apiresponse.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController()
@RequestMapping(value = "/structure/etablissement")
public class EtablissementController {

  @Autowired
  private EtablissementRepository<Etablissement> etablissementRepository;
  @Autowired
  private FonctionRepository<Fonction> fonctionRepository;

  @GetMapping()
  public ApiResponse getEtablissements() {
    return new ApiResponse("", etablissementRepository.findAllEtablissements());
  }

  @GetMapping(value = "/{id}")
  public ApiResponse getEtablissement(@PathVariable Long id) {
    EtablissementDto etablissement = etablissementRepository.findByIdEtablissement(id);
    etablissement.setFilieres(fonctionRepository.findByStructure(id));

    return new ApiResponse("", etablissement);
  }

}