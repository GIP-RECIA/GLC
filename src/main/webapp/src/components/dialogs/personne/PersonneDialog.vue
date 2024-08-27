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
import PersonneDialogAdditional from '@/components/dialogs/personne/PersonneDialogAdditional.vue';
import PersonneDialogInfo from '@/components/dialogs/personne/PersonneDialogInfo.vue';
import { useConfigurationStore, usePersonneStore, useStructureStore } from '@/stores/index.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState';
import { Tabs } from '@/types/enums/Tabs.ts';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';

const configurationStore = useConfigurationStore();
const { isEditAllowed } = configurationStore;
const { structureTab } = storeToRefs(configurationStore);

const stcuctureStore = useStructureStore();
const { fonction } = storeToRefs(stcuctureStore);

const personneStore = usePersonneStore();
const { currentPersonne, isCurrentPersonne, dialogState, dialogTitle } = storeToRefs(personneStore);

const modelValue = computed<boolean>({
  get() {
    return isCurrentPersonne.value;
  },
  set() {},
});
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024">
    <v-card rounded="xl">
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ dialogTitle }}</v-toolbar-title>
        <template v-if="dialogState == PersonneDialogState.Info" #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" class="me-1" @click="isCurrentPersonne = false" />
        </template>
      </v-toolbar>

      <personne-dialog-info v-if="dialogState == PersonneDialogState.Info" :personne="currentPersonne" />

      <personne-dialog-additional
        v-if="
          dialogState == PersonneDialogState.ManageAdditional &&
          structureTab == Tabs.SchoolStaff &&
          fonction?.customMapping &&
          isEditAllowed(currentPersonne ? currentPersonne.etat : '')
        "
        :personne="currentPersonne"
        :filieres="fonction?.customMapping?.filieres"
      />
    </v-card>
  </v-dialog>
</template>
