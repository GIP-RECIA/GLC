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

import type { Ref } from 'vue'
import { useMutation, useQuery, useQueryCache } from '@pinia/colada'
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import {
  getUser,
  removeUserOneAdditional,
  searchUser,
  setUserAdditional,
  setUserOneAdditional,
} from '@/services/api/index.ts'

const queryCache = useQueryCache()

function useUserQuery() {
  const route = useRoute()
  const userId = computed(() => Number(route.params.userId))

  return useQuery(() => ({
    key: ['user', userId.value],
    query: () => getUser(userId.value),
    enabled: !Number.isNaN(userId.value),
    staleTime: 1000 * 60 * 30,
  }))
}

function useSearchUserQuery(
  name: Ref<string>,
  params?: {
    etab?: string
    not_in_etab?: number
    staff?: boolean
    check_rights?: boolean
  },
) {
  return useQuery(() => ({
    key: ['user', 'search', name.value],
    query: () => searchUser(name.value, params),
    enabled: !!name.value,
    staleTime: 1000 * 60 * 5,
  }))
}

function useSetUserAdditionalMutation() {
  return useMutation({
    mutation: setUserAdditional,
    onSuccess(_data, vars, _context) {
      queryCache.invalidateQueries({ key: ['structure', vars.structureId] })
    },
    onSettled: (_data, _error, vars, _context) => {
      queryCache.invalidateQueries({ key: ['user', vars.id] })
    },
  })
}

function useSetUserOneAdditionalMutation() {
  return useMutation({
    mutation: setUserOneAdditional,
    onSuccess(_data, vars, _context) {
      queryCache.invalidateQueries({ key: ['structure', vars.structureId] })
    },
    onSettled: (_data, _error, vars, _context) => {
      queryCache.invalidateQueries({ key: ['user', vars.id] })
    },
  })
}

function useRemoveUserOneAdditionalMutation() {
  return useMutation({
    mutation: removeUserOneAdditional,
    onSuccess(_data, vars, _context) {
      queryCache.invalidateQueries({ key: ['structure', vars.structureId] })
    },
    onSettled: (_data, _error, vars, _context) => {
      queryCache.invalidateQueries({ key: ['user', vars.id] })
    },
  })
}

export {
  useRemoveUserOneAdditionalMutation,
  useSearchUserQuery,
  useSetUserAdditionalMutation,
  useSetUserOneAdditionalMutation,
  useUserQuery,
}
