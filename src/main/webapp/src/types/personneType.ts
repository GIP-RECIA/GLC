import type { PersonneFonction } from '@/types/fonctionType.ts';

export type Personne = {
  id: number;
  uid?: string;
  etat: string;
  anneeScolaire: string;
  categorie: string;
  civilite: string;
  source: string;
  cn: string;
  sn: string;
  dateNaissance?: string;
  email?: string;
  givenName: string;
  patronyme?: string;
  login: string;
  dateFin?: string;
  dateSourceModification: string;
  fonctions?: Array<PersonneFonction>;
  additionalFonctions?: Array<PersonneFonction>;
  dateSuppression?: string;
};

export type SimplePersonne = {
  id: number;
  uid?: string;
  etat: string;
  categorie: string;
  source: string;
  cn: string;
  sn: string;
  email?: string;
  dateSuppression?: string;
};
