import type { PersonneFonction } from "@/types/fonctionType";

export type Personne = {
  id: number;
  etat: string;
  anneeScolaire: Date;
  categorie: string;
  civilite: string;
  source: string;
  cn: string;
  dateNaissance: Date;
  displayName: string;
  email: string;
  givenName: string;
  numBureau: string;
  patronyme?: string;
  sn: string;
  titre: string;
  uid?: string;
  uuid?: string;
  emailPersonnel: string;
  listeRouge: boolean;
  forceEtat: string;
  idEduConnect: string;
  login: string;
  fonctions: Array<PersonneFonction>;
  additionalFonctions: Array<PersonneFonction>;
};

export type SimplePersonne = {
  id: number;
  etat: string;
  categorie: string;
  source: string;
  givenName: string;
  patronyme: string;
  uid?: string;
};

export type SearchPersonne = SimplePersonne & { displayName: string };
