/**
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

import type { enumValues } from '../enumValuesTypes'

export enum CategoriePersonne {
  Eleve = 'Eleve',
  Enseignant = 'Enseignant',
  Non_enseignant_collectivite_locale = 'Non_enseignant_collectivite_locale',
  Non_enseignant_etablissement = 'Non_enseignant_etablissement',
  Non_enseignant_service_academique = 'Non_enseignant_service_academique',
  Personne_relation_eleve = 'Personne_relation_eleve',
  Personnel_exterieur = 'Personnel_exterieur',
  Responsable_Entreprise = 'Responsable_Entreprise',
  Tuteur_stage = 'Tuteur_stage',
}

export const categoriePersonneMap: Record<CategoriePersonne, enumValues> = {
  [CategoriePersonne.Eleve]: {
    i18n: 'page.user.category.student',
  },
  [CategoriePersonne.Enseignant]: {
    i18n: 'page.user.category.teacher',
  },
  [CategoriePersonne.Non_enseignant_collectivite_locale]: {
    i18n: 'page.user.category.nonTeacherLocalCommunity',
  },
  [CategoriePersonne.Non_enseignant_etablissement]: {
    i18n: 'page.user.category.nonTeacherSchool',
  },
  [CategoriePersonne.Non_enseignant_service_academique]: {
    i18n: 'page.user.category.nonTeacherAcademicService',
  },
  [CategoriePersonne.Personne_relation_eleve]: {
    i18n: 'page.user.category.personRelationshipStudent',
  },
  [CategoriePersonne.Personnel_exterieur]: {
    i18n: 'page.user.category.externalStaff',
  },
  [CategoriePersonne.Responsable_Entreprise]: {
    i18n: 'page.user.category.companyManager',
  },
  [CategoriePersonne.Tuteur_stage]: {
    i18n: 'page.user.category.internshipTutor',
  },
}
