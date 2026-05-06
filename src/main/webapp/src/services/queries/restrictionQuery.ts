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
import type { StructureRestriction } from '@/types/index.ts'
import { useMutation, useQuery, useQueryCache } from '@pinia/colada'
import { getRestrictions, saveRestrictions } from '@/services/api/index.ts'

function useRestrictionsQuery(id: Ref<number>) {
  return useQuery(() => ({
    key: ['restrictions', id.value],
    query: () => getRestrictions(id.value),
    enabled: !!id.value && id.value !== -1,
    staleTime: 1000 * 60 * 10,
  }))
}

function useSaveRestrictionsMutation() {
  const queryCache = useQueryCache()

  return useMutation({
    mutation: saveRestrictions,
    onMutate: (vars, _context) => {
      queryCache.setQueryData<StructureRestriction>(
        ['restrictions', vars.id],
        vars.body,
      )
    },
    onError: (_error, vars, _context) => {
      queryCache.invalidateQueries({ key: ['restrictions', vars.id] })
    },
  })
}

export {
  useRestrictionsQuery,
  useSaveRestrictionsMutation,
}
