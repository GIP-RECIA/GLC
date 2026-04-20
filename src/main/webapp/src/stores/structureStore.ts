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
  SearchStructure,
} from '@/types/index.ts'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getEtablissements } from '@/services/api/index.ts'
import { errorHandler } from '@/utils/index.ts'

export const useStructureStore = defineStore('structure', () => {
  const etabs = ref<SearchStructure[] | undefined>()

  const isInit = computed<boolean>(() => (
    etabs.value
      ? etabs.value.length > 0
      : false
  ))

  /**
   * Initialise `etabs`
   */
  const init = async (): Promise<void> => {
    if (!isInit.value) {
      try {
        etabs.value = await getEtablissements()
      }
      catch (e) {
        errorHandler(e, 'initStructureStore')
      }
    }
  }

  return {
    etabs,
    init,
  }
})
