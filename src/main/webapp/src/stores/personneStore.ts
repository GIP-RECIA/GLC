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
import type { Personne, PersonneFonction } from '@/types'
import { getPersonne } from '@/services/api'
import { PersonneDialogState } from '@/types/enums'
import { errorHandler } from '@/utils'
import debounce from 'lodash.debounce'
import { defineStore, storeToRefs } from 'pinia'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useConfigurationStore } from './configurationStore.ts'
import { useStructureStore } from './structureStore.ts'

export const usePersonneStore = defineStore('personne', () => {
  const configurationStore = useConfigurationStore()
  const structureStore = useStructureStore()

  const { t } = useI18n()

  const currentPersonne = ref<Personne | undefined>()

  /**
   * Etat du composant PersonneDialog
   */
  const isCurrentPersonne = ref<boolean>(false)

  /**
   * Initialise `currentPersonne`
   *
   * @param id        Identifiant de la personne
   * @param showModal Ouvre ou non la modale un fois les données chargées
   */
  const initCurrentPersonne = async (id: number, showModal: boolean = true): Promise<void> => {
    configurationStore.isLoading = true
    try {
      const response = await getPersonne(id)
      currentPersonne.value = response.data
      isCurrentPersonne.value = showModal
    }
    catch (e) {
      errorHandler(e, 'initCurrentPersonne')
    }
    configurationStore.isLoading = false
  }

  const refreshCurrentPersonne = async (): Promise<void> => {
    const personneId = currentPersonne.value?.id
    if (personneId) {
      configurationStore.isLoading = true
      structureStore.refreshCurrentStructure()
      try {
        const response = await getPersonne(personneId)
        currentPersonne.value = response.data
      }
      catch (e) {
        errorHandler(e, 'refreshCurrentPersonne')
      }
      configurationStore.isLoading = false
    }
  }

  /**
   * Retourne la liste des fonctions au sein d'une structure.
   */
  const fonctionsByStructure = (
    fonctions: Array<PersonneFonction> | undefined,
    structureId: number | undefined,
  ): Array<PersonneFonction> => {
    if (!fonctions || !structureId)
      return []
    const result = fonctions.filter(({ structure }) => structure === structureId)

    return result && result.length > 0 ? result : []
  }

  /**
   * Retourne un objet contenant la liste des fonctions et fonctions additionnelles pour la structure courrante.
   */
  const personneStructure = computed<{
    fonctions: Array<PersonneFonction> | undefined
    additionalFonctions: Array<PersonneFonction> | undefined
  }>(() => {
    const { currentStructureId } = storeToRefs(configurationStore)
    if (!currentPersonne.value || !currentStructureId.value) {
      return {
        fonctions: undefined,
        additionalFonctions: undefined,
      }
    }

    return {
      fonctions: fonctionsByStructure(currentPersonne.value.fonctions, currentStructureId.value),
      additionalFonctions: fonctionsByStructure(currentPersonne.value.additionalFonctions, currentStructureId.value),
    }
  })

  const dialogState = ref<PersonneDialogState>(PersonneDialogState.Info)

  const dialogTitle = computed<string>(() => {
    if (!currentPersonne.value)
      return ''
    const { cn } = currentPersonne.value
    switch (dialogState.value) {
      case PersonneDialogState.ManageAdditional:
      case PersonneDialogState.ManageAdditionalMultiple:
        return `${t('person.information.additionalFunction', 2)} - ${cn}`
      case PersonneDialogState.Info:
      default:
        return cn
    }
  })

  const editFunction = ref<PersonneFonction | undefined>()

  /**
   * Mode de rattachement
   */
  const attachMode = ref<boolean>(false)

  /**
   * Reset lors de la fermeture du dialog
   */
  watch(isCurrentPersonne, (newValue) => {
    if (!newValue) {
      const reset = debounce(() => {
        currentPersonne.value = undefined
        dialogState.value = PersonneDialogState.Info
        editFunction.value = undefined
        attachMode.value = false
      }, 200)
      reset()
    }
  })

  return {
    currentPersonne,
    isCurrentPersonne,
    initCurrentPersonne,
    refreshCurrentPersonne,
    personneStructure,
    dialogState,
    dialogTitle,
    editFunction,
    attachMode,
  }
})
