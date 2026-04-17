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

import type { IconDefinition } from '@fortawesome/free-solid-svg-icons'
import { faFloppyDisk, faLink, faLinkSlash } from '@fortawesome/free-solid-svg-icons'
import { computed, ref } from 'vue'
import { isEmpty } from '@/utils/index.ts'

function useSaveAttachDetach() {
  /**
   * Active le bouton de détachement si la personne n'as ni fonction, ni
   * fonctions complémentaires dans la structure et que la valeur est à true
   */
  const isDetach = ref<boolean>(false)

  const saveButton = computed<{
    i18n: string
    icon: IconDefinition
    color: string
  }>(() => {
    if (isEmpty([])) {
      if (isEmpty([])) {
        return {
          i18n: 'button.attach',
          icon: faLink,
          color: 'success',
        }
      }
      if (isDetach.value) {
        return {
          i18n: 'button.detach',
          icon: faLinkSlash,
          color: 'error',
        }
      }
    }
    return {
      i18n: 'button.save',
      icon: faFloppyDisk,
      color: 'success',
    }
  })

  return {
    isDetach,
    saveButton,
  }
}

export {
  useSaveAttachDetach,
}
