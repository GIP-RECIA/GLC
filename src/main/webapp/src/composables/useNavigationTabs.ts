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
  RouteLocationAsRelativeGeneric,
  RouteLocationNormalizedGeneric,
} from 'vue-router'
import { useQueryCache } from '@pinia/colada'
import { useSessionStorage } from '@vueuse/core'
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useStructureQueryOptions, useUserQueryOptions } from '@/services/queries'
import { concatenate } from '@/utils/index.ts'

interface TabItemT {
  id: string
  name: string
  to: RouteLocationAsRelativeGeneric
}

const items = useSessionStorage<TabItemT[]>(
  `${__APP_SLUG__}.account.tabs`,
  [],
)

export function useNavigationTabs() {
  const route = useRoute()
  const router = useRouter()

  const isItems = computed<boolean>(() => items.value.length > 0)

  function getFromIndex(
    from: RouteLocationNormalizedGeneric,
  ): number {
    return items.value.findIndex(i =>
      from.fullPath === router.resolve(i.to).fullPath,
    )
  }

  function addItem(item: TabItemT, index: number = -1): void {
    const exists = items.value.some(i => i.id === item.id)
    if (exists)
      return

    if (index === -1) {
      items.value.push(item)
      return
    }

    items.value.splice(index + 1, 0, item)
  }

  function removeItem(id: string): void {
    const index = items.value.findIndex(i => i.id === id)

    if (index === -1)
      return

    const itemToRemove = items.value[index]

    const isActive = (
      route.fullPath === router.resolve(itemToRemove.to).fullPath
    )

    items.value.splice(index, 1)

    if (!isActive)
      return

    if (items.value.length === 0) {
      router.push({ name: 'account' })

      return
    }

    const nextIndex
      = index < items.value.length
        ? index
        : items.value.length - 1

    const nextItem = items.value[nextIndex]

    router.push(nextItem.to)
  }

  async function loadStructure(
    from: RouteLocationNormalizedGeneric,
    structureId: number,
  ): Promise<void> {
    const queryCache = useQueryCache()
    const { data, error } = await queryCache.refresh(
      queryCache.ensure(
        useStructureQueryOptions(structureId),
      ),
    )
    if (error)
      throw error

    addItem({
      id: `structure-${structureId}`,
      name: concatenate(
        [data!.nom, data!.type, data!.uai],
        ' ',
      ),
      to: {
        name: 'structure',
        params: { structureId },
      },
    }, getFromIndex(from))
  }

  async function loadUser(
    from: RouteLocationNormalizedGeneric,
    userId: number,
  ): Promise<void> {
    const queryCache = useQueryCache()
    const { data, error } = await queryCache.refresh(
      queryCache.ensure(
        useUserQueryOptions(userId),
      ),
    )
    if (error)
      throw error

    addItem({
      id: `user-${userId}`,
      name: data!.cn,
      to: {
        name: 'user',
        params: { userId },
      },
    }, getFromIndex(from))
  }

  return {
    items,
    isItems,
    addItem,
    removeItem,
    getFromIndex,
    loadStructure,
    loadUser,
  }
}
