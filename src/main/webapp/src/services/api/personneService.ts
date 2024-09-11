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
import type { FonctionForm } from '@/types';
import { instance as axios } from '@/utils';

const getPersonne = async (id: number) => await axios.get(`/api/personne/${id}`);

const searchPersonne = async (name: string) => await axios.get(`/api/personne?name=${name}`);

const setPersonneAdditional = async (
  id: number,
  structureId: number,
  toAddFunctions: Array<string>,
  toDeleteFunctions: Array<string>,
  requiredAction: string,
) =>
  await axios.post(`/api/personne/${id}/fonction`, { structureId, toAddFunctions, toDeleteFunctions, requiredAction });

const setPersonneAdditionalWithId = async (
  id: number,
  structureId: number,
  toAddFunction: string,
  requiredAction: string,
) => await setPersonneAdditional(id, structureId, [toAddFunction], [], requiredAction);

const setPersonneAdditionalWithCode = async (
  id: number,
  structureId: number,
  additionalCode: string,
  requiredAction: string,
) => await axios.post(`/api/personne/${id}/fonction`, { structureId, additionalCode, requiredAction });

const setPersonneAdditionalV2 = async (
  id: number,
  structureId: number,
  toAdd: FonctionForm | null,
  toDelete: string | null,
) => await axios.post(`/api/personne/${id}/fonction/v2`, { structureId, toAdd, toDelete });

const addPersonneAdditionalV2 = async (id: number, structureId: number, toAdd: FonctionForm | undefined) =>
  await setPersonneAdditionalV2(id, structureId, toAdd ?? null, null);

const deletePersonneAdditionalV2 = async (id: number, structureId: number, toDelete: string | undefined) =>
  await setPersonneAdditionalV2(id, structureId, null, toDelete ?? null);

export {
  getPersonne,
  searchPersonne,
  setPersonneAdditional,
  setPersonneAdditionalWithId,
  setPersonneAdditionalWithCode,
  setPersonneAdditionalV2,
  addPersonneAdditionalV2,
  deletePersonneAdditionalV2,
};
