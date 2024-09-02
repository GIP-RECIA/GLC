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

export {
  getPersonne,
  searchPersonne,
  setPersonneAdditional,
  setPersonneAdditionalWithId,
  setPersonneAdditionalWithCode,
};
