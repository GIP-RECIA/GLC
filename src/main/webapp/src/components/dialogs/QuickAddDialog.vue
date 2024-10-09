<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import type { SimplePersonne } from '@/types'
import PersonneSearch from '@/components/search/personne/PersonneSearch.vue'
import { useSaveAttachDetach } from '@/composables'
import { setPersonneAdditionalWithCode, setPersonneAdditionalWithId } from '@/services/api'
import { useConfigurationStore, usePersonneStore, useStructureStore } from '@/stores'
import { errorHandler, fonctionsToId } from '@/utils'
import debounce from 'lodash.debounce'
import { storeToRefs } from 'pinia'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { toast } from 'vue3-toastify'

const configurationStore = useConfigurationStore()
const { isEditAllowed } = configurationStore
const { currentStructureId, isQuickAdd, requestAdd } = storeToRefs(configurationStore)

const personneStore = usePersonneStore()
const { initCurrentPersonne } = personneStore
const { currentPersonne, personneStructure } = storeToRefs(personneStore)

const structureStore = useStructureStore()
const { refreshCurrentStructure } = structureStore

const { t } = useI18n()

const modelValue = computed<boolean>({
  get() {
    return isQuickAdd.value
  },
  set() {},
})

const user = ref<SimplePersonne | undefined>()
const selectedUser = computed<SimplePersonne | undefined>({
  get() {
    return user.value
  },
  set(selected) {
    currentPersonne.value = undefined
    if (selected) {
      user.value = selected
      initCurrentPersonne(selected.id, false)
    }
    else {
      user.value = undefined
    }
  },
})

const canSave = computed<boolean>(() => {
  const functions: Array<string> = [
    ...new Set(
      fonctionsToId(personneStructure.value.fonctions).concat(
        fonctionsToId(personneStructure.value.additionalFonctions),
      ),
    ),
  ]
  const alreadyHasFunction = requestAdd.value?.function && functions.includes(requestAdd.value.function)
  if (alreadyHasFunction)
    toast.error(t('toast.'))

  return currentPersonne.value ? !alreadyHasFunction : false
})

const { saveButton } = useSaveAttachDetach()

async function save(): Promise<void> {
  if (requestAdd.value?.function) {
    try {
      if (requestAdd.value.type === 'id') {
        await setPersonneAdditionalWithId(
          currentPersonne.value!.id,
          currentStructureId.value!,
          requestAdd.value.function,
          saveButton.value.i18n.split('.')[1],
        )
      }
      else {
        await setPersonneAdditionalWithCode(
          currentPersonne.value!.id,
          currentStructureId.value!,
          requestAdd.value.function,
          saveButton.value.i18n.split('.')[1],
        )
      }
      closeAndResetModal(true)
    }
    catch (e) {
      errorHandler(e)
      closeAndResetModal(false)
    }
  }
}

function closeAndResetModal(success?: boolean): void {
  if (success) {
    refreshCurrentStructure()
    toast.success(t('toast.additional.success.save'))
  }
  else if (!success && success !== undefined) {
    toast.error(t('toast.additional.error.save'))
  }

  if (isQuickAdd.value)
    isQuickAdd.value = false
  const reset = debounce(() => {
    currentPersonne.value = undefined
  }, 200)
  reset()
}

watch(currentPersonne, (newValue) => {
  if (isQuickAdd.value && newValue) {
    if (!isEditAllowed(newValue.etat))
      toast.error(t('toast.editStatusDenied'))
  }
})
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024 / 2">
    <v-card rounded="xl">
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">
          {{ requestAdd?.i18n }}
        </v-toolbar-title>
        <template #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" class="me-1" @click="closeAndResetModal()" />
        </template>
      </v-toolbar>
      <v-card-text class="py-0">
        <PersonneSearch
          v-model="selectedUser"
          :search-list="requestAdd?.searchList"
          variant="outlined"
          :class="[currentPersonne && isEditAllowed(currentPersonne.etat) ? 'mb-4' : 'mb-6']"
        />
      </v-card-text>
      <v-card-actions v-if="currentPersonne && isEditAllowed(currentPersonne.etat)">
        <v-spacer />
        <v-btn
          :color="saveButton.color"
          :prepend-icon="saveButton.icon"
          :text="t(saveButton.i18n)"
          :disabled="!canSave"
          @click="save"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
