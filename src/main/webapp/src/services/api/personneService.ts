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

async function setPersonneAdditional(
  id: number,
  structureId: number,
  toAddFunctions: string[],
  toDeleteFunctions: string[],
  requiredAction: string,
) {
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

function setPersonneAdditionalWithId(
  id: number,
  structureId: number,
  toAddFunction: string,
  requiredAction: string,
) {
  return setPersonneAdditional(
    id,
    structureId,
    [toAddFunction],
    [],
    requiredAction,
  )
}

async function setPersonneAdditionalWithCode(
  id: number,
  structureId: number,
  additionalCode: string,
  requiredAction: string,
) {
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

async function setPersonneAdditionalV2(
  id: number,
  structureId: number,
  toAdd: FonctionForm | null,
  toDelete: string | null,
) {
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

function addPersonneAdditionalV2(
  id: number,
  structureId: number,
  toAdd: FonctionForm | undefined,
) {
  return setPersonneAdditionalV2(
    id,
    structureId,
    toAdd ?? null,
    null,
  )
}

function deletePersonneAdditionalV2(
  id: number,
  structureId: number,
  toDelete: string | undefined,
) {
  return setPersonneAdditionalV2(
    id,
    structureId,
    null,
    toDelete ?? null,
  )
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
