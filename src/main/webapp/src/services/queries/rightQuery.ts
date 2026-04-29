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
import type { RightMember, ServiceRights } from '@/types/index.ts'
import { useMutation, useQuery, useQueryCache } from '@pinia/colada'
import { getRights, updateRight } from '@/services/api/index.ts'

const queryCache = useQueryCache()

function useRightsQuery(id: Ref<number>) {
  return useQuery(() => ({
    key: ['rights', id.value],
    query: () => getRights(id.value),
    enabled: !!id.value && id.value !== -1,
    staleTime: 1000 * 60 * 10,
  }))
}

function useUpdateRightMutation() {
  return useMutation({
    mutation: updateRight,
    onMutate: (vars) => {
      const {
        service,
        role,
        membersToAdd,
        membersToRemove,
      } = vars

      const data = queryCache.getQueryData<ServiceRights[]>(['rights', vars.id])

      const newData = data?.map((d) => {
        if (d.service !== service)
          return d

        const rights = d.rights.map((right) => {
          if (right.role !== role)
            return right

          const currentMembers = right.currentMembers
            .filter(({ id }) => !membersToRemove.includes(id))
          const knownGroups = [
            ...right.mandatoryGroups,
            ...right.possibleGroups,
          ]
          const toAdd: RightMember[] = membersToAdd.map((add) => {
            return knownGroups.find(({ id }) => id === add)
              ?? {
                id: add,
                displayName: add,
                user: add.startsWith('F'),
                external: false,
              }
          })
          currentMembers.push(...toAdd)

          return { ...right, currentMembers }
        })

        return { ...d, rights }
      })

      queryCache.setQueryData(['rights', vars.id], newData)
    },
    onError: (_error, vars) => {
      queryCache.invalidateQueries({ key: ['rights', vars.id] })
    },
  })
}

export {
  useRightsQuery,
  useUpdateRightMutation,
}
