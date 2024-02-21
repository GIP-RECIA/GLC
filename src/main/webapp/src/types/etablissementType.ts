import type { SimplePersonne } from './personneType.ts';
import type { Adresse } from '@/types/adresseType.ts';
import type { Alert } from '@/types/alertType.ts';
import type { Filiere } from '@/types/filiereType.ts';

export type Etablissement = {
  id: number;
  uai: string;
  etat: string;
  etatAlim: string;
  source: string;
  anneeScolaire: string;
  adresse: Adresse;
  categorie: string;
  type?: string;
  mail: string;
  nom: string;
  nomCourt: string;
  siren: string;
  siteWeb: string;
  modeleLogin: string;
  logo: string;
  personnes: Array<SimplePersonne>;
  filieres: Array<Filiere>;
  withoutFunctions?: Array<SimplePersonne>;
  alerts: Array<Alert>;
  permission?: string;
};

export type SimpleEtablissement = {
  id: number;
  uai: string;
  categorie: string;
  type?: string;
  nom: string;
  nomCourt: string;
  ville?: string;
  siren: string;
};
