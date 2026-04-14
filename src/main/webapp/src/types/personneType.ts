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

import type { CategoriePersonne } from './enums/CategoriePersonne.ts'
import type { Etat } from './enums/Etat.ts'
import type { StructureForUser } from './etablissementType.ts'
import type { FonctionForm } from './fonctionType.ts'

export interface Personne {
  id: number
  uid?: string
  etat: Etat
  anneeScolaire: string
  categorie: CategoriePersonne
  civilite: string
  source: string
  cn: string
  sn: string
  givenName: string
  patronyme?: string
  dateNaissance?: string
  email?: string
  login: string
  photo?: string
  idPronote?: string
  listeRouge: boolean
  dateFin?: string
  dateModification?: string
  dateAcquittement?: string
  dateSuppression?: string
  dateSourceModification: string
  listeStructures: StructureForUser[]
  relations: Relation[]
}

export interface Relation {
  categorieRelation: string
  personneEnRelation: SimplePersonne
  holder: boolean
}

export interface SimplePersonne {
  id: number
  uid?: string
  etat: Etat
  categorie: CategoriePersonne
  source: string
  cn: string
  sn: string
  email?: string
  dateSuppression?: string
}

export interface SetPersonneAdditionalParams {
  id: number
  structureId: number
  toAddFunctions: FonctionForm[]
  toDeleteFunctions: FonctionForm[]
  requiredAction: string
}

export type SetPersonneAdditionalWithIdParams = Omit<
  SetPersonneAdditionalParams,
  'toAddFunctions'
  | 'toDeleteFunctions'
> & {
  toAddFunction: FonctionForm
}

export type SetPersonneAdditionalWithCodeParams = Omit<
  SetPersonneAdditionalParams,
  'toAddFunctions'
  | 'toDeleteFunctions'
> & {
  additionalCode: FonctionForm
}
