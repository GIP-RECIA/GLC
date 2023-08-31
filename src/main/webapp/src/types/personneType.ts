import type { PersonneFonction } from '@/types/fonctionType';

export type Personne = {
  id: number;
  uid?: string;
  etat: string;
  anneeScolaire: Date;
  categorie: string;
  civilite: string;
  source: string;
  cn: string;
  sn: string;
  dateNaissance: Date;
  email: string;
  givenName: string;
  patronyme?: string;
  login: string;
  fonctions?: Array<PersonneFonction>;
  additionalFonctions?: Array<PersonneFonction>;
};

export type SimplePersonne = {
  id: number;
  uid?: string;
  etat: string;
  categorie: string;
  source: string;
  cn: string;
  sn: string;
};
