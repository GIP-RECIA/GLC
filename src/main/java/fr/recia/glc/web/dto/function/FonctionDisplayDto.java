package fr.recia.glc.web.dto.function;

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

import fr.recia.glc.db.entities.education.Discipline;
import fr.recia.glc.db.entities.fonction.TypeFonctionFiliere;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class FonctionDisplayDto {

    private FiliereDisplayDto filiere;
    private DisciplineDisplayDto discipline;
    private Date dateFin;
    // TODO : gérer la date de début
    private Date dateDebut;

    public FonctionDisplayDto(TypeFonctionFiliere filiere, Discipline discipline, Date dateFin) {
        this.filiere = new FiliereDisplayDto(filiere.getId(), filiere.getLibelleFiliere());
        this.discipline = new DisciplineDisplayDto(discipline.getId(), discipline.getDisciplinePoste());
        this.dateFin = dateFin;
    }
}
