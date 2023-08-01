import type { PersonneFonction } from "@/types/fonctionType";

export type Personne = {
  id: number;
  uid?: string;
  uuid?: string;
  etat: string;
  anneeScolaire: Date;
  categorie: string;
  civilite: string;
  source: string;
  cn: string;
  sn: string;
  dateNaissance: Date;
  displayName: string;
  email: string;
  givenName: string;
  numBureau: string;
  patronyme?: string;
  titre: string;
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
  uid?: string;
  etat: string;
  categorie: string;
  source: string;
  cn: string;
  sn: string;
  givenName: string;
  patronyme: string;
};
