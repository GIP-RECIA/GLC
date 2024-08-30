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
import type { Adresse } from './adresseType.ts';
import type { Alert } from './alertType.ts';
import type { Filiere } from './filiereType.ts';
import type { SimplePersonne } from './personneType.ts';

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
