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
import { usePersonneStore } from '@/stores'
import { isEmpty } from '@/utils'
import { storeToRefs } from 'pinia'
import { computed, ref } from 'vue'

function useSaveAttachDetach() {
  const personneStore = usePersonneStore()
  const { personneStructure } = storeToRefs(personneStore)

  /**
   * Active le bouton de détachement si la personne n'as ni fonction, ni
   * fonctions complémentaires dans la structure et que la valeur est à true
   */
  const isDetach = ref<boolean>(false)

  const saveButton = computed<{ i18n: string, icon: string, color: string }>(() => {
    if (isEmpty(personneStructure.value.fonctions)) {
      if (isEmpty(personneStructure.value.additionalFonctions)) {
        return {
          i18n: 'button.attach',
          icon: 'fas fa-link',
          color: 'success',
        }
      }
      if (isDetach.value) {
        return {
          i18n: 'button.detach',
          icon: 'fas fa-link-slash',
          color: 'error',
        }
      }
    }
    return {
      i18n: 'button.save',
      icon: 'fas fa-floppy-disk',
      color: 'success',
    }
  })

  return { isDetach, saveButton }
}

export { useSaveAttachDetach }
