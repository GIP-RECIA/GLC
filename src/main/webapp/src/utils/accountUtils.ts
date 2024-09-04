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
import { useConfigurationStore } from '@/stores';
import type { PersonneFonction, endInfo, enumValues } from '@/types';
import { CategoriePersonne, Etat } from '@/types/enums';
import { differenceInCalendarMonths, isPast } from 'date-fns';
import { storeToRefs } from 'pinia';

const isLocal = (source: string): boolean => source.startsWith('SarapisUi_');

const getIcon = (source: string): string => `${isLocal(source) ? 'far' : 'fas'} fa-user`;

const getEtat = (etat: string): enumValues => {
  switch (etat) {
    case Etat.Inconnu.toString():
      return { i18n: 'person.status.inconnu', color: '#9E9E9E', icon: 'fas fa-user-secret' };
    case Etat.Invalide.toString():
      return { i18n: 'person.status.invalid', color: '#A5D6A7' };
    case Etat.Valide.toString():
      return { i18n: 'person.status.valid', color: '#4CAF50' };
    case Etat.Unpublished.toString():
      return { i18n: 'person.status.unpublished', color: '#607D8B' };
    case Etat.Bloque.toString():
      return { i18n: 'person.status.locked', color: '#795548', icon: 'fas fa-user-lock' };
    case Etat.Delete.toString():
      return { i18n: 'person.status.deleted', color: '#EF9A9A' };
    case Etat.Deleting.toString():
      return { i18n: 'person.status.deleting', color: '#FF5722', icon: 'fas fa-user-clock' };
    case Etat.Incertain.toString():
      return { i18n: 'person.status.uncertain', color: '#FFEB3B' };
    case Etat.Incertain_Export_Add.toString():
      return { i18n: 'person.status.uncertainExportAdd', color: '#FFEB3B' };
    case Etat.Incertain_Export_Delete.toString():
      return { i18n: 'person.status.uncertainExportDelete', color: '#FFEB3B' };
    case Etat.Incertain_Export_Modify.toString():
      return { i18n: 'person.status.uncertainExportModify', color: '#FFEB3B' };
    default:
      throw new Error(`Non-existent etat in switch: ${etat}`);
  }
};

const getCategoriePersonne = (categorie: string): enumValues => {
  switch (categorie) {
    case CategoriePersonne.Eleve.toString():
      return { i18n: 'person.category.student' };
    case CategoriePersonne.Enseignant.toString():
      return { i18n: 'person.category.teacher' };
    case CategoriePersonne.Non_enseignant_collectivite_locale.toString():
      return { i18n: 'person.category.nonTeacherLocalCommunity' };
    case CategoriePersonne.Non_enseignant_etablissement.toString():
      return { i18n: 'person.category.nonTeacherSchool' };
    case CategoriePersonne.Non_enseignant_service_academique.toString():
      return { i18n: 'person.category.nonTeacherAcademicService' };
    case CategoriePersonne.Personne_relation_eleve.toString():
      return { i18n: 'person.category.personRelationshipStudent' };
    case CategoriePersonne.Personnel_exterieur.toString():
      return { i18n: 'person.category.externalStaff' };
    case CategoriePersonne.Responsable_Entreprise.toString():
      return { i18n: 'person.category.companyManager' };
    case CategoriePersonne.Tuteur_stage.toString():
      return { i18n: 'person.category.internshipTutor' };
    default:
      throw new Error(`Non-existent categorie personne in switch: ${categorie}`);
  }
};

const getDateFin = (date: string): endInfo => {
  const configurationStore = useConfigurationStore();
  const { configuration } = storeToRefs(configurationStore);

  if (isPast(date))
    return {
      date,
      isPast: true,
      i18n: 'person.function.hourglass.end',
      color: '',
      icon: 'fas fa-hourglass-end',
    };
  const months: number = differenceInCalendarMonths(date, new Date());
  if (months < (configuration.value?.front.endFunctionWarning ?? 2))
    return {
      date,
      months,
      isPast: false,
      i18n: 'person.function.hourglass.half',
      color: 'warning',
      icon: 'fas fa-hourglass-half',
    };

  return {
    date,
    months,
    isPast: false,
    i18n: 'person.function.hourglass.start',
    color: 'primary',
    icon: 'fas fa-hourglass-start',
  };
};

const fonctionToId = (fonction: PersonneFonction): string => `${fonction.filiere}-${fonction.discipline}`;

const filiereDisciplineToId = (filiere: number, discipline: number): string => `${filiere}-${discipline}`;

const fonctionsToId = (fonctions: Array<PersonneFonction> | undefined): Array<string> =>
  fonctions ? fonctions.map((fonction) => fonctionToId(fonction)) : [];

const idToFonction = (id: string): { filiere: number; discipline: number } => {
  const fonctionIds = id.split('-').map((id) => parseInt(id));

  return {
    filiere: fonctionIds[0],
    discipline: fonctionIds[1],
  };
};

export {
  isLocal,
  getIcon,
  getEtat,
  getCategoriePersonne,
  getDateFin,
  fonctionToId,
  filiereDisciplineToId,
  fonctionsToId,
  idToFonction,
};
