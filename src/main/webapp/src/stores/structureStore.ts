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
  AccountUser,
  CustomMapping,
  SearchStructure,
  SourceFonction,
  Structure,
} from '@/types/index.ts'
import { isEmpty } from 'lodash-es'
import { defineStore, storeToRefs } from 'pinia'
import { computed, ref } from 'vue'
import { getEtablissement, getEtablissements } from '@/services/api/index.ts'
import { errorHandler } from '@/utils/index.ts'
import { useConfigurationStore } from './configurationStore.ts'

export const useStructureStore = defineStore('structure', () => {
  const configurationStore = useConfigurationStore()

  const etabs = ref<SearchStructure[] | undefined>()
  const currentEtab = ref<Structure | undefined>()

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

  /**
   * Initialise `currentEtab`
   * @param id Identifiant de la structure
   */
  const initCurrentEtab = async (
    id: number,
  ): Promise<void> => {
    currentEtab.value = undefined
    try {
      currentEtab.value = await getEtablissement(id)
    }
    catch (e) {
      errorHandler(e, 'initCurrentEtab')
    }
  }

  const personnes = computed<AccountUser[] | undefined>(
    () => currentEtab.value?.personnes,
  )

  const staff = computed(() => {
    const { configuration } = storeToRefs(configurationStore)

    const getStaff = (categorie?: string): AccountUser[] | undefined => {
      return personnes.value
        ?.filter(personne => personne.categorie === categorie)
    }

    return {
      teaching: getStaff(configuration.value?.front.staff.teaching),
      school: getStaff(configuration.value?.front.staff.school),
      collectivity: getStaff(configuration.value?.front.staff.collectivity),
      academic: getStaff(configuration.value?.front.staff.academic),
    }
  })

  const fonction = computed<SourceFonction | undefined>(() => {
    const { fonctions } = storeToRefs(configurationStore)

    const fonction: SourceFonction | undefined = fonctions.value?.find(
      fonction => fonction.source === currentEtab.value?.source,
    )
    const customMapping: CustomMapping | undefined = isEmpty(fonction?.customMapping)
      ? undefined
      : fonction.customMapping

    return fonction
      ? { ...fonction, customMapping }
      : undefined
  })

  return {
    etabs,
    currentEtab,
    init,
    initCurrentEtab,
    personnes,
    staff,
    fonction,
  }
})
