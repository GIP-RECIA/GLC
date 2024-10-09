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
import type { Discipline } from './disciplineType.ts'
import type { Filiere } from './filiereType.ts'

export interface SourceFonction {
  source: string
  filieres: Array<Filiere>
  customMapping?: CustomMapping
}

export interface CustomMapping {
  filieres: Array<Filiere>
  disciplines: Array<Discipline>
}

export interface PersonneFonction {
  filiere: number
  discipline: number
  source: string
  structure: number
  dateFin?: string
}

export interface FonctionForm {
  fonction?: string
  date?: string
}
