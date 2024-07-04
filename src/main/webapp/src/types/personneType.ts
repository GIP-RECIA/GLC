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
