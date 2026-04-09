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
import type { PersonneFonction } from '@/types/index.ts'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n'
import FonctionForm from '@/components/forms/FonctionForm.vue'
import { useManageAdditional } from '@/composables/index.ts'
import { useStructureStore } from '@/stores/index.ts'
import { fonctionsToId } from '@/utils/index.ts'

defineProps<{
  fonction?: PersonneFonction
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'save': []
}>()

const modelValue = defineModel<boolean>({ required: true })

const structureStore = useStructureStore()
const { fonction } = storeToRefs(structureStore)

const { t } = useI18n()

const {
  data,
  canSave,
  saveButton,
  deleteButton,
  canDelete,
  onSave,
  onCancel,
  onDelete,
} = useManageAdditional()

function close() {
  emit('update:modelValue', false)
}
</script>

<template>
  <v-dialog
    v-model="modelValue"
    scrollable
    :max-width="1024"
  >
    <v-card rounded="xl">
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title>
          <h1>
            {{ }}
          </h1>
        </v-toolbar-title>
        <template #append>
          <v-btn
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            class="me-1"
            @click="close"
          />
        </template>
      </v-toolbar>

      <v-card-text class="pt-0 py-3 manage-additional-dialog-container">
        <FonctionForm
          v-model="data.selected[0]"
          :filieres="fonction?.customMapping?.filieres"
          :disabled="fonctionsToId(data.disabled)"
          :disable-fonction-edit="!!data.baseSelection[0]"
        />
      </v-card-text>

      <v-card-actions>
        <v-btn
          v-if="canDelete"
          color="error"
          :prepend-icon="deleteButton.icon"
          :text="t(deleteButton.i18n)"
          @click="onDelete"
        />
        <v-spacer />
        <v-btn
          color="secondary"
          prepend-icon="fas fa-xmark"
          :text="t('button.cancel')"
          @click="onCancel"
        />
        <v-btn
          :color="saveButton.color"
          :prepend-icon="saveButton.icon"
          :text="t(saveButton.i18n)"
          :disabled="!canSave"
          @click="onSave"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
