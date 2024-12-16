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

import type { FonctionForm } from '@/types'
import { instance as axios } from '@/utils'

const getPersonne = async (id: number) => await axios.get(`/api/personne/${id}`)

const searchPersonne = async (name: string) => await axios.get(`/api/personne?name=${name}`)

async function setPersonneAdditional(
  id: number,
  structureId: number,
  toAddFunctions: Array<string>,
  toDeleteFunctions: Array<string>,
  requiredAction: string,
) {
  return await axios.post(
    `/api/personne/${id}/fonction`,
    { structureId, toAddFunctions, toDeleteFunctions, requiredAction },
  )
}

async function setPersonneAdditionalWithId(
  id: number,
  structureId: number,
  toAddFunction: string,
  requiredAction: string,
) {
  return await setPersonneAdditional(id, structureId, [toAddFunction], [], requiredAction)
}

async function setPersonneAdditionalWithCode(
  id: number,
  structureId: number,
  additionalCode: string,
  requiredAction: string,
) {
  return await axios.post(`/api/personne/${id}/fonction`, { structureId, additionalCode, requiredAction })
}

async function setPersonneAdditionalV2(
  id: number,
  structureId: number,
  toAdd: FonctionForm | null,
  toDelete: string | null,
) {
  return await axios.post(`/api/personne/${id}/fonction/v2`, { structureId, toAdd, toDelete })
}

async function addPersonneAdditionalV2(id: number, structureId: number, toAdd: FonctionForm | undefined) {
  return await setPersonneAdditionalV2(id, structureId, toAdd ?? null, null)
}

async function deletePersonneAdditionalV2(id: number, structureId: number, toDelete: string | undefined) {
  return await setPersonneAdditionalV2(id, structureId, null, toDelete ?? null)
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
