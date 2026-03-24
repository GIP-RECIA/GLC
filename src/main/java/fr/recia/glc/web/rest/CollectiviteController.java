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
import fr.recia.glc.security.GLCUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/api/structure/collectivite")
public class CollectiviteController {

    /**
     * Récupère les informations sur une collectivité avec toutes les personnes dedans
     */
    @GetMapping(value = "/{id}")
    // TODO : route spécifique pour récupérer les infos d'une collectivité
    public ResponseEntity<EtablissementDto> getCollectivite(@AuthenticationPrincipal GLCUser principal, @PathVariable Long id) {
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
