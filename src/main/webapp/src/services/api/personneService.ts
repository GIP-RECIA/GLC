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

import type {
  Personne,
  SetPersonneAdditionalParams,
  SetPersonneAdditionalWithCodeParams,
  SetPersonneAdditionalWithIdParams,
  SimplePersonne,
} from '@/types/index.ts'
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

export {
  getPersonne,
  searchPersonne,
  setPersonneAdditional,
  setPersonneAdditionalWithCode,
  setPersonneAdditionalWithId,
}
