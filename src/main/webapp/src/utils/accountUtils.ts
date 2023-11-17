import type { enumValues } from '@/types/enumValuesType.ts';
import { CategoriePersonne } from '@/types/enums/CategoriePersonne.ts';
import { Etat } from '@/types/enums/Etat.ts';
import type { PersonneFonction } from '@/types/fonctionType.ts';

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

const toIdentifier = (fonctions: Array<PersonneFonction> | undefined): Array<string> =>
  fonctions ? fonctions.map((fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`) : [];

export { isLocal, getIcon, getEtat, getCategoriePersonne, toIdentifier };
