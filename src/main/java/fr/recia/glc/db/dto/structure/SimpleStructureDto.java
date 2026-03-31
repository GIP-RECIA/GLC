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
package fr.recia.glc.db.dto.structure;

import fr.recia.glc.db.entities.structure.AStructure;
import fr.recia.glc.db.entities.structure.CollectiviteLocale;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.entities.structure.ServiceAcademique;
import fr.recia.glc.db.enums.CategorieStructure;
import lombok.Data;

@Data
public class SimpleStructureDto {

    private Long id;
    private String uai;
    private CategorieStructure categorie;
    private String type;
    private String nom;
    private String nomCourt;
    private String ville;
    private String siren;

    public SimpleStructureDto(AStructure aStructure) {
        String[] split = aStructure.getNom().split("\\$");
        if (split.length > 2) {
            this.type = split[0];
            this.nom = split[1];
            this.ville = split[2];
        } else {
            this.nom = aStructure.getNom();
            this.ville = aStructure.getAdresse().getVille();
        }
        this.id = aStructure.getId();
        if(aStructure.getCategorie().equals(CategorieStructure.Etablissement)){
            this.uai = ((Etablissement) aStructure).getUai();
        }
        else if(aStructure.getCategorie().equals(CategorieStructure.Service_academique)){
            this.uai = ((ServiceAcademique) aStructure).getUai();
        }
        this.categorie = aStructure.getCategorie();
        this.nomCourt = aStructure.getNomCourt();
        this.siren = aStructure.getSiren();
    }

    public SimpleStructureDto(Etablissement etablissement) {
        String[] split = etablissement.getNom().split("\\$");
        if (split.length > 2) {
            this.type = split[0];
            this.nom = split[1];
            this.ville = split[2];
        } else {
            this.nom = etablissement.getNom();
            this.ville = etablissement.getAdresse().getVille();
        }

        this.id = etablissement.getId();
        this.uai = etablissement.getUai();
        this.categorie = etablissement.getCategorie();
        this.nomCourt = etablissement.getNomCourt();
        this.siren = etablissement.getSiren();
    }

    public SimpleStructureDto(CollectiviteLocale collectiviteLocale) {
        this.id = collectiviteLocale.getId();
        this.siren = collectiviteLocale.getSiren();
        this.nom = collectiviteLocale.getNom();
        this.categorie = collectiviteLocale.getCategorie();
        this.nomCourt = collectiviteLocale.getNomCourt();
    }

}
