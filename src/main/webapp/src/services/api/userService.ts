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
  SearchUser,
  SetUserAdditionalParams,
  SetUserAdditionalWithCodeParams,
  SetUserAdditionalWithIdParams,
  User,
} from '@/types/index.ts'
import { instance as axios } from '@/utils/index.ts'

async function getUser(id: number) {
  return (
    await axios.get<User>(
      `/api/personne/${id}`,
    )
  ).data
}

async function deleteUser(id: number) {
  return (
    await axios.delete<void>(
      `/api/personne/${id}`,
    )
  ).status === 200
}

async function forceDeleteUser(id: number) {
  return (
    await axios.delete<void>(
      `/api/personne/${id}/force`,
    )
  ).status === 200
}

async function undoDeleteUser(id: number) {
  return (
    await axios.delete<void>(
      `/api/personne/${id}/undo`,
    )
  ).status === 200
}

async function lockUser(id: number) {
  return (
    await axios.put<void>(
      `/api/personne/${id}/lock`,
    )
  ).status === 200
}

async function unlockUser(id: number) {
  return (
    await axios.put<void>(
      `/api/personne/${id}/unlock`,
    )
  ).status === 200
}

async function searchUser(name: string) {
  return (
    await axios.get<SearchUser[]>(
      '/api/personne',
      {
        params: {
          name,
        },
      },
    )
  ).data
}

async function setUserAdditional({
  id,
  structureId,
  toAddFunctions,
  toDeleteFunctions,
  requiredAction,
}: SetUserAdditionalParams) {
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

function setUserAdditionalWithId({
  id,
  structureId,
  toAddFunction,
  requiredAction,
}: SetUserAdditionalWithIdParams) {
  return setUserAdditional({
    id,
    structureId,
    toAddFunctions: [toAddFunction],
    toDeleteFunctions: [],
    requiredAction,
  })
}

async function setUserAdditionalWithCode({
  id,
  structureId,
  additionalCode,
  requiredAction,
}: SetUserAdditionalWithCodeParams) {
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
  deleteUser,
  forceDeleteUser,
  getUser,
  lockUser,
  searchUser,
  setUserAdditional,
  setUserAdditionalWithCode,
  setUserAdditionalWithId,
  undoDeleteUser,
  unlockUser,
}
