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
package fr.recia.glc.web.dto.user;

import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.personne.APersonne;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.Etat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@EqualsAndHashCode
public class CardPersonneDto {

    private Long id;
    private Etat etat;
    private boolean local;
    private String cn;
    private Date dateSuppression;

    public CardPersonneDto(SimplePersonneDto simplePersonneDto) {
        this.id = simplePersonneDto.getId();
        this.etat = simplePersonneDto.getEtat();
        this.local = simplePersonneDto.getSource().startsWith("SarapisUi_");
        this.cn = simplePersonneDto.getCn();
        this.dateSuppression = simplePersonneDto.getDateSuppression();
    }

}
