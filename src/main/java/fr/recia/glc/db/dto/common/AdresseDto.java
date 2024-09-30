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
package fr.recia.glc.db.dto.common;

import fr.recia.glc.db.entities.common.Adresse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdresseDto {

  private String adresse;
  private String codePostal;
  private String ville;
  private String boitePostale;
  private String pays;

  public AdresseDto(Adresse adresse) {
    this.adresse = adresse.getAdresse();
    this.codePostal = adresse.getCodePostal();
    this.ville = adresse.getVille();
    this.boitePostale = adresse.getBoitePostale();
    this.pays = adresse.getPays();
  }

}
