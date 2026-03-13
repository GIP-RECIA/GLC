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

import { useMutation, useQuery } from '@pinia/colada'
import {
  addPersonneAdditionalV2,
  deletePersonneAdditionalV2,
  getPersonne,
  searchPersonne,
  setPersonneAdditional,
  setPersonneAdditionalV2,
  setPersonneAdditionalWithCode,
  setPersonneAdditionalWithId,
} from '@/services/api/index.ts'

function usePersonneQuery(id: number) {
  return useQuery({
    key: ['personne', id],
    query: () => getPersonne(id),
  })
}

function useSearchPersonneQuery(name: string) {
  return useQuery({
    key: ['personne', 'search', name],
    query: () => searchPersonne(name),
  })
}

function useSetPersonneAdditionalMutation() {
  return useMutation({
    mutation: setPersonneAdditional,
  })
}

function useSetPersonneAdditionalWithIdMutation() {
  return useMutation({
    mutation: setPersonneAdditionalWithId,
  })
}

function useSetPersonneAdditionalWithCodeMutation() {
  return useMutation({
    mutation: setPersonneAdditionalWithCode,
  })
}

function useSetPersonneAdditionalV2Mutation() {
  return useMutation({
    mutation: setPersonneAdditionalV2,
  })
}

function useAddPersonneAdditionalV2Mutation() {
  return useMutation({
    mutation: addPersonneAdditionalV2,
  })
}

function useDeletePersonneAdditionalV2Mutation() {
  return useMutation({
    mutation: deletePersonneAdditionalV2,
  })
}

export {
  useAddPersonneAdditionalV2Mutation,
  useDeletePersonneAdditionalV2Mutation,
  usePersonneQuery,
  useSearchPersonneQuery,
  useSetPersonneAdditionalMutation,
  useSetPersonneAdditionalV2Mutation,
  useSetPersonneAdditionalWithCodeMutation,
  useSetPersonneAdditionalWithIdMutation,
}
