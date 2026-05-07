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

package fr.recia.glc.db.dto.gestion;

import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.web.dto.user.AlertPersonneDto;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class IncertainPersonneDto {

    private AlertPersonneDto personne;
    private List<IncertainDto> incertains;

    public IncertainPersonneDto(APersonne aPersonne){
        this.personne = new AlertPersonneDto(aPersonne);
        this.incertains = new ArrayList<>();
    }
}
