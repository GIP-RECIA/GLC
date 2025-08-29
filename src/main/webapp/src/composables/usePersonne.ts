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
import type { enumValues, Personne } from '@/types'
import { format, getYear } from 'date-fns'
import { storeToRefs } from 'pinia'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useConfigurationStore, usePersonneStore, useStructureStore } from '@/stores'
import { CategoriePersonne } from '@/types/enums'
import { getEtat } from '@/utils'

function usePersonne(personne: Ref<Personne | undefined> = ref(undefined)) {
  const configurationStore = useConfigurationStore()
  const { isEditAllowed, getLoginOffice } = configurationStore

  const stcuctureStore = useStructureStore()
  const { fonction } = storeToRefs(stcuctureStore)

  const personneStore = usePersonneStore()
  const { currentPersonne } = storeToRefs(personneStore)

  const thisPersonne = computed<Personne | undefined>(() => personne.value ?? currentPersonne.value)

  const { t } = useI18n()

  const etat = computed<enumValues>(() => {
    if (!thisPersonne.value)
      return { i18n: '', color: '' }
    return getEtat(thisPersonne.value.etat)
  })

  const schoolYear = computed<string | undefined>(() => {
    if (!thisPersonne.value)
      return undefined
    const year = getYear(thisPersonne.value.anneeScolaire)

    return `${year}/${year + 1}`
  })

  const login = computed<{ i18n: string, info?: string }>(() => {
    if (!thisPersonne.value)
      return { i18n: '', info: '' }
    const office = getLoginOffice(thisPersonne.value.categorie, thisPersonne.value.source)

    return {
      i18n: office ? t('externalLogin') : thisPersonne.value.login,
      info: office ? t(`office.${office}`) : undefined,
    }
  })

  const suppressDate = computed<string | undefined>(() => {
    return thisPersonne.value?.dateSuppression ? format(thisPersonne.value.dateSuppression, 'P') : undefined
  })

  const hasFunctions = computed<boolean>(() => {
    if (!thisPersonne.value)
      return false
    return [
      CategoriePersonne.Enseignant.toString(),
      CategoriePersonne.Non_enseignant_etablissement.toString(),
      CategoriePersonne.Non_enseignant_collectivite_locale.toString(),
    ].includes(thisPersonne.value.categorie)
  })

  const canEditAdditionals = computed<boolean>(
    () => (fonction.value?.customMapping ?? false) && isEditAllowed(thisPersonne.value?.etat ?? ''),
  )

  return { etat, schoolYear, login, suppressDate, hasFunctions, canEditAdditionals }
}

export { usePersonne }
