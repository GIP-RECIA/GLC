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
import fr.recia.glc.db.dto.common.AdresseDto;
import fr.recia.glc.db.dto.education.DisciplineDto;
import fr.recia.glc.db.dto.fonction.FonctionDto;
import fr.recia.glc.db.dto.fonction.TypeFonctionFiliereDto;
import fr.recia.glc.db.dto.personne.SimplePersonneDto;
import fr.recia.glc.db.enums.CategoriePersonne;
import fr.recia.glc.db.enums.CategorieStructure;
import fr.recia.glc.db.enums.Etat;
import fr.recia.glc.db.enums.EtatAlim;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static fr.recia.glc.configuration.Constants.SANS_OBJET;

@Slf4j
@Getter
@Setter
@EqualsAndHashCode
public class EtablissementDto {

  private Long id;
  private String uai;
  private Etat etat;
  private EtatAlim etatAlim;
  private String source;
  private Date anneeScolaire;
  private AdresseDto adresse;
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
  private List<TypeFonctionFiliereDto> teachingStaff;
  private List<TypeFonctionFiliereDto> schoolStaff;
  private List<TypeFonctionFiliereDto> collectivityStaff;
  private List<TypeFonctionFiliereDto> academicStaff;
  private List<SimplePersonneDto> personnes;
  @Setter(AccessLevel.NONE)
  private List<SimplePersonneDto> withoutFunctions;
  private List<SimplePersonneDto> withoutFunctionsTeaching;
  private List<SimplePersonneDto> withoutFunctionsSchool;
  private List<SimplePersonneDto> withoutFunctionsCollectivity;
  private List<SimplePersonneDto> withoutFunctionsAcademic;
  private List<AlertDto> alerts;
  private String permission;

  public EtablissementDto(Long id, String uai, Etat etat, EtatAlim etatAlim, String source, Date anneeScolaire,
                          String adresse, String codePostal, String ville, String boitePostale, String pays,
                          CategorieStructure categorie, String mail, String nom, String nomCourt, String siren,
                          String siteWeb, String modeleLogin, String logo) {
    this.id = id;
    this.uai = uai;
    this.etat = etat;
    this.etatAlim = etatAlim;
    this.source = source;
    this.anneeScolaire = anneeScolaire;
    this.adresse = new AdresseDto(adresse, codePostal, ville, boitePostale, pays);
    this.categorie = categorie;
    this.mail = mail;
    this.nom = nom;
    this.nomCourt = nomCourt;
    this.siren = siren;
    this.siteWeb = siteWeb;
    this.modeleLogin = modeleLogin;
    this.logo = logo;
  }

  public EtablissementDto(Long id, String uai, Etat etat, EtatAlim etatAlim, String source, Date anneeScolaire,
                          AdresseDto adresse, CategorieStructure categorie, String mail, String nom,
                          String nomCourt, String siren, String siteWeb, String modeleLogin, String logo) {
    this.id = id;
    this.uai = uai;
    this.etat = etat;
    this.etatAlim = etatAlim;
    this.source = source;
    this.anneeScolaire = anneeScolaire;
    this.adresse = adresse;
    this.categorie = categorie;
    this.mail = mail;
    this.nom = nom;
    this.nomCourt = nomCourt;
    this.siren = siren;
    this.siteWeb = siteWeb;
    this.modeleLogin = modeleLogin;
    this.logo = logo;
  }

  public void setFilieres(
    Long structureId, String source, List<SimplePersonneDto> personnes, List<FonctionDto> fonctions,
    List<TypeFonctionFiliereDto> typesFonctionFiliere, List<DisciplineDto> disciplines
  ) {
    if (fonctions.isEmpty()) return;

    setFilieres(typesFonctionFiliere.stream()
      // Ajout des disciplines aux filières
      .map(typeFonctionFiliere -> {
        // Filtre les fonctions de la filière
        List<FonctionDto> fonctionsInFiliere = fonctions.stream()
          .filter(fonction -> Objects.equals(fonction.getFiliere(), typeFonctionFiliere.getId()))
          .collect(Collectors.toList());
        // Liste les ID de disciplines de la filière
        Set<Long> disciplineIds = fonctionsInFiliere.stream()
          .map(FonctionDto::getDisciplinePoste)
          .collect(Collectors.toSet());
        // Liste les disciplines de la filière
        List<DisciplineDto> disciplinesInFiliere = disciplines.stream()
          .filter(discipline -> disciplineIds.contains(discipline.getId()) && !Objects.equals(discipline.getDisciplinePoste(), SANS_OBJET))
          .collect(Collectors.toList());
        // Ajout des personnes aux disciplines
        disciplinesInFiliere = disciplinesInFiliere.stream()
          .map(discipline -> {
            // Liste des ID de personne de la discipline
            Set<Long> personneIds = fonctionsInFiliere.stream()
              .filter(fonction -> Objects.equals(fonction.getDisciplinePoste(), discipline.getId()))
              .map(FonctionDto::getPersonne)
              .collect(Collectors.toSet());
            // Liste les personnes de la discipline
            List<SimplePersonneDto> personnesInDiscipline = personnes.stream()
              .filter(personne -> personneIds.contains(personne.getId()))
              .collect(Collectors.toList());
            discipline.setPersonnes(personnesInDiscipline);

            return discipline;
          })
          .collect(Collectors.toList());
        typeFonctionFiliere.setDisciplines(disciplinesInFiliere);

        return typeFonctionFiliere;
      })
      // Retrait des filières sans disciplines
      .filter(typeFonctionFiliere -> !typeFonctionFiliere.getDisciplines().isEmpty() && !Objects.equals(typeFonctionFiliere.getLibelleFiliere(), SANS_OBJET))
      .collect(Collectors.toList())
    );

    setTeachingStaff(getFiliereByCategoriePersonne(CategoriePersonne.Enseignant));
    setSchoolStaff(getFiliereByCategoriePersonne(CategoriePersonne.Non_enseignant_etablissement));
    setCollectivityStaff(getFiliereByCategoriePersonne(CategoriePersonne.Non_enseignant_collectivite_locale));
    setAcademicStaff(getFiliereByCategoriePersonne(CategoriePersonne.Non_enseignant_service_academique));
  }

  private List<TypeFonctionFiliereDto> getFiliereByCategoriePersonne(CategoriePersonne categoriePersonne) {
    final List<TypeFonctionFiliereDto> tmpFilieres = filieres.stream()
      .map(TypeFonctionFiliereDto::new)
      .collect(Collectors.toList());

    return tmpFilieres.stream()
      .map(typeFonctionFiliere -> {
        typeFonctionFiliere.setDisciplines(
          typeFonctionFiliere.getDisciplines().stream()
            .map(discipline -> {
              discipline.setPersonnes(
                discipline.getPersonnes().stream()
                  .filter(personne -> personne.getCategorie() == categoriePersonne)
                  .collect(Collectors.toList())
              );

              return discipline;
            })
            .filter(discipline -> !discipline.getPersonnes().isEmpty())
            .collect(Collectors.toList())
        );

        return typeFonctionFiliere;
      })
      .filter(typeFonctionFiliere -> !typeFonctionFiliere.getDisciplines().isEmpty())
      .collect(Collectors.toList());
  }

  public void setWithoutFunctions(List<SimplePersonneDto> personnes) {
    this.withoutFunctions = personnes;
    setWithoutFunctionsTeaching(getPersonneByCategoriePersonne(CategoriePersonne.Enseignant));
    setWithoutFunctionsSchool(getPersonneByCategoriePersonne(CategoriePersonne.Non_enseignant_etablissement));
    setWithoutFunctionsCollectivity(getPersonneByCategoriePersonne(CategoriePersonne.Non_enseignant_collectivite_locale));
    setWithoutFunctionsAcademic(getPersonneByCategoriePersonne(CategoriePersonne.Non_enseignant_service_academique));
  }

  private List<SimplePersonneDto> getPersonneByCategoriePersonne(CategoriePersonne categoriePersonne) {
    final List<SimplePersonneDto> tmpPersonnes = withoutFunctions.stream()
      .map(SimplePersonneDto::new)
      .collect(Collectors.toList());

    return tmpPersonnes.stream()
      .filter(personne -> personne.getCategorie() == categoriePersonne)
      .collect(Collectors.toList());
  }

}
