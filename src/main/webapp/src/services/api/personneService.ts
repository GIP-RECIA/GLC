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

import type { FonctionForm, Personne, SimplePersonne } from '@/types/index.ts'
import { instance as axios } from '@/utils/index.ts'

async function getPersonne(id: number) {
  return (
    await axios.get<Personne>(
      `/api/personne/${id}`,
    )
  ).data
}

async function searchPersonne(name: string) {
  return (
    await axios.get<SimplePersonne[]>(
      '/api/personne',
      {
        params: {
          name,
        },
      },
    )
  ).data
}

interface SetPersonneAdditionalParams {
  id: number
  structureId: number
  toAddFunctions: string[]
  toDeleteFunctions: string[]
  requiredAction: string
}

async function setPersonneAdditional({
  id,
  structureId,
  toAddFunctions,
  toDeleteFunctions,
  requiredAction,
}: SetPersonneAdditionalParams) {
  return !!(
    await axios.post<void>(
      `/api/personne/${id}/fonction`,
      {
        structureId,
        toAddFunctions,
        toDeleteFunctions,
        requiredAction,
      },
    )
  )
}

type SetPersonneAdditionalWithIdParams = Omit<
  SetPersonneAdditionalParams,
  'toAddFunctions'
  | 'toDeleteFunctions'
> & {
  toAddFunction: string
}

function setPersonneAdditionalWithId({
  id,
  structureId,
  toAddFunction,
  requiredAction,
}: SetPersonneAdditionalWithIdParams) {
  return setPersonneAdditional({
    id,
    structureId,
    toAddFunctions: [toAddFunction],
    toDeleteFunctions: [],
    requiredAction,
  })
}

type SetPersonneAdditionalWithCodeParams = Omit<
  SetPersonneAdditionalParams,
  'toAddFunctions'
  | 'toDeleteFunctions'
> & {
  additionalCode: string
}

async function setPersonneAdditionalWithCode({
  id,
  structureId,
  additionalCode,
  requiredAction,
}: SetPersonneAdditionalWithCodeParams) {
  return !!(
    await axios.post<void>(
      `/api/personne/${id}/fonction`,
      {
        structureId,
        additionalCode,
        requiredAction,
      },
    )
  )
}

interface SetPersonneAdditionalV2Params {
  id: number
  structureId: number
  toAdd: FonctionForm | null
  toDelete: string | null
}

async function setPersonneAdditionalV2({
  id,
  structureId,
  toAdd,
  toDelete,
}: SetPersonneAdditionalV2Params) {
  return !!(
    await axios.post<void>(
      `/api/personne/${id}/fonction/v2`,
      {
        structureId,
        toAdd,
        toDelete,
      },
    )
  )
}

type AddPersonneAdditionalV2Params = Omit<
  SetPersonneAdditionalV2Params,
  'toDelete'
>

function addPersonneAdditionalV2({
  id,
  structureId,
  toAdd,
}: AddPersonneAdditionalV2Params) {
  return setPersonneAdditionalV2({
    id,
    structureId,
    toAdd: toAdd ?? null,
    toDelete: null,
  })
}

type DeletePersonneAdditionalV2Params = Omit<
  SetPersonneAdditionalV2Params,
  'toAdd'
>

function deletePersonneAdditionalV2({
  id,
  structureId,
  toDelete,
}: DeletePersonneAdditionalV2Params) {
  return setPersonneAdditionalV2({
    id,
    structureId,
    toAdd: null,
    toDelete: toDelete ?? null,
  })
}

export {
  addPersonneAdditionalV2,
  deletePersonneAdditionalV2,
  getPersonne,
  searchPersonne,
  setPersonneAdditional,
  setPersonneAdditionalV2,
  setPersonneAdditionalWithCode,
  setPersonneAdditionalWithId,
}
