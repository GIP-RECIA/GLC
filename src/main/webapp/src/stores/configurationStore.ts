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

/* eslint-disable ts/no-use-before-define */
import type {
  Configuration,
  Filiere,
  SourceFonction,
} from '@/types/index.ts'
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { getConfiguration, getFonctions } from '@/services/api/index.ts'
import { errorHandler } from '@/utils/index.ts'

export const useConfigurationStore = defineStore('configuration', () => {
  const configuration = ref<Configuration | undefined>()
  const fonctions = ref<SourceFonction[] | undefined>()

  /**
   * Initialise `configuration`
   */
  const init = async (): Promise<void> => {
    if (!isInit.value) {
      try {
        configuration.value = await getConfiguration()
      }
      catch (e) {
        errorHandler(e, 'initConfigurationStore')
      }
    }
  }

  const initFonctions = async (): Promise<void> => {
    if (!isInitFonctions.value) {
      try {
        fonctions.value = await getFonctions()
      }
      catch (e) {
        errorHandler(e, 'initFonctionStore')
      }
    }
  }

  const isInit = computed<boolean>(() => configuration.value !== undefined)
  const isInitFonctions = computed<boolean>(() => fonctions.value !== undefined)

  const allFilieres = computed<Filiere[] | undefined>(() => {
    return fonctions.value
      ? fonctions.value.find(fonction => fonction.source === 'ALL')?.filieres
      : undefined
  })

  const loginOffices = computed(() => configuration.value?.front.loginOffices)

  const getLoginOffice = (
    categorie: string,
    source: string,
  ): string | undefined => {
    if (loginOffices.value) {
      const sources = loginOffices.value
        .filter(office => office.source === source)
      if (sources.length <= 0)
        return undefined
      else if (sources.length > 1)
        throw new Error(`Can not resolve guichet for source ${source}`)

      const offices: string[] = sources[0].guichets
        .filter(guichet => guichet.categoriesPersonne.includes(categorie))
        .map(guichet => guichet.nom)
      if (offices.length > 1)
        throw new Error(`Can not resolve guichet for categorie ${categorie}`)

      return offices[0]
    }
    return undefined
  }

  return {
    configuration,
    fonctions,
    init,
    initFonctions,
    isInit,
    isInitFonctions,
    allFilieres,
    getLoginOffice,
  }
})
