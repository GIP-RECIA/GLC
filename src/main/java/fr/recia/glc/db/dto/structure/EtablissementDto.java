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

import fr.recia.glc.db.dto.AlertDto;
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.entities.common.Adresse;
import fr.recia.glc.db.entities.structure.Etablissement;
import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.EtatAlim;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.SANS_OBJET;

@Data
public class EtablissementDto {

  private Long id;
  private String uai;
  private Etat etat;
  private EtatAlim etatAlim;
  private String source;
  private Date anneeScolaire;
  private Adresse adresse;
  private CategorieStructure categorie;
  private String type;
  private String mail;
  private String nom;
  private String nomCourt;
  private String siren;
  private String siteWeb;
  private String modeleLogin;
  private String logo;

  private List<TypeFonctionFiliereDto> filieres;
  private List<SimplePersonneDto> personnes;
  private List<SimplePersonneDto> withoutFunctions;
  private List<AlertDto> alerts;
  private String permission;

  public EtablissementDto(Etablissement etablissement) {
    this.id = etablissement.getId();
    this.uai = etablissement.getUai();
    this.etat = etablissement.getEtat();
    this.etatAlim = etablissement.getEtatAlim();
    this.source = etablissement.getCleJointure().getSource();
    this.anneeScolaire = etablissement.getAnneeScolaire();
    this.adresse = etablissement.getAdresse();
    this.categorie = etablissement.getCategorie();
    this.mail = etablissement.getMail();
    this.nomCourt = etablissement.getNomCourt();
    this.siren = etablissement.getSiren();
    this.siteWeb = etablissement.getSiteWeb();
    this.modeleLogin = etablissement.getModeleLogin();
    this.logo = etablissement.getLogo();

    String[] split = etablissement.getNom().split("\\$");
    if (split.length > 1) {
      this.type = split[0];
      this.nom = split[1];
    } else {
//      this.type = etablissement.getType().getLibelle();
      this.nom = etablissement.getNom();
    }
  }

  public void setFilieres(
    List<FonctionDto> fonctions,
    List<TypeFonctionFiliereDto> typesFonctionFiliere,
    List<DisciplineDto> disciplines
  ) {
    if (fonctions.isEmpty()) {
      setFilieres(List.of());
      return;
    }

    final List<TypeFonctionFiliereDto> filieresWithDisciplines = typesFonctionFiliere.stream()
      .map(tff -> {
        final Set<Long> disciplineIds = fonctions.stream()
          .filter(fonction -> Objects.equals(fonction.getFiliere(), tff.getId()))
          .map(FonctionDto::getDiscipline)
          .collect(Collectors.toSet());

        final List<DisciplineDto> disciplinesWithPersonnes = disciplines.stream()
          .map(DisciplineDto::new)
          .filter(discipline -> disciplineIds.contains(discipline.getId()))
          .map(discipline -> {
            final Set<Long> personneIds = fonctions.stream()
              .filter(fonction -> Objects.equals(fonction.getFiliere(), tff.getId())
                && Objects.equals(fonction.getDiscipline(), discipline.getId())
              )
              .map(FonctionDto::getPersonne)
              .collect(Collectors.toSet());

            final List<SimplePersonneDto> personnesInDiscipline = personnes.stream()
              .filter(personne -> {
                if (Objects.equals(discipline.getDisciplinePoste(), SANS_OBJET)) {
                  final List<FonctionDto> personneOtherFonctions = fonctions.stream()
                    .filter(fonction -> Objects.equals(fonction.getPersonne(), personne.getId()))
                    .filter(fonction -> !(Objects.equals(fonction.getFiliere(), tff.getId()) && Objects.equals(fonction.getDiscipline(), discipline.getId())))
                    .collect(Collectors.toList());

                  if (!personneOtherFonctions.isEmpty()) return false;
                }

                return personneIds.contains(personne.getId());
              })
              .collect(Collectors.toList());
            discipline.setPersonnes(personnesInDiscipline);

            return discipline;
          })
          .filter(discipline -> discipline.getPersonnes() != null && !discipline.getPersonnes().isEmpty())
          .collect(Collectors.toList());
        tff.setDisciplines(disciplinesWithPersonnes);

        return tff;
      })
      .filter(tff -> !tff.getDisciplines().isEmpty())
      .collect(Collectors.toList());
    setFilieres(filieresWithDisciplines);
  }

}
