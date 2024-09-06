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
import CheckboxForm from '@/components/forms/CheckboxForm.vue';
import { useManageAdditional } from '@/composables';
import { useStructureStore } from '@/stores';
import { PersonneDialogState } from '@/types/enums';
import { storeToRefs } from 'pinia';
import { useI18n } from 'vue-i18n';

const stcuctureStore = useStructureStore();
const { fonction } = storeToRefs(stcuctureStore);

const { t } = useI18n();

const { data, canSave, saveButton, onSave, onCancel } = useManageAdditional(
  PersonneDialogState.ManageAdditionalMultiple,
);
</script>

<template>
  <v-card-text class="py-0">
    <checkbox-form v-model="data.selected" :filieres="fonction?.customMapping?.filieres" :disabled="data.disabled" />
  </v-card-text>

  <v-card-actions>
    <v-spacer />
    <v-btn color="secondary" prepend-icon="fas fa-xmark" :text="t('button.cancel')" @click="onCancel" />
    <v-btn
      :color="saveButton.color"
      :prepend-icon="saveButton.icon"
      :text="t(saveButton.i18n)"
      :disabled="!canSave"
      @click="onSave"
    />
  </v-card-actions>
</template>
