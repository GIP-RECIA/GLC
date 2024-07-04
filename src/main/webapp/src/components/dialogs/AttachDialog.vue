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
import PersonneSearch from '@/components/search/personne/PersonneSearch.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState';
import type { SimplePersonne } from '@/types/personneType.ts';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { personneDialogState, isAttach, attachMode } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const { currentPersonne } = storeToRefs(personneStore);

const { t } = useI18n();

const modelValue = computed<boolean>({
  get() {
    return isAttach.value;
  },
  set() {},
});

const selectedUser = computed<SimplePersonne | undefined>({
  get() {
    return undefined;
  },
  set(user) {
    currentPersonne.value = undefined;
    if (user) {
      attachMode.value = true;
      isAttach.value = false;
      personneDialogState.value = PersonneDialogState.ManageAdditional;
      initCurrentPersonne(user.id, true);
    }
  },
});
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024 / 2">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ t('button.attach') }}</v-toolbar-title>
        <template #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" @click="isAttach = false" />
        </template>
      </v-toolbar>
      <v-card-text class="pt-0 pb-6">
        <personne-search v-model="selectedUser" search-type="OUT" variant="outlined" />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
