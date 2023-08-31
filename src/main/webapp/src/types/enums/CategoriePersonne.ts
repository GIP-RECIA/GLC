import type { enumValues } from '@/types/enumValuesType';

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

export { getCategoriePersonne };
