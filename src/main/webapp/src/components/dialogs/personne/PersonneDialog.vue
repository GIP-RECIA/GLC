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
import PersonneDialogInfo from './PersonneDialogInfo.vue';
import PersonneDialogManageAdditional from './PersonneDialogManageAdditional.vue';
import { usePersonne } from '@/composables';
import { useConfigurationStore, usePersonneStore } from '@/stores';
import { PersonneDialogState, Tabs } from '@/types/enums';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';

const configurationStore = useConfigurationStore();
const { structureTab } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { currentPersonne, isCurrentPersonne, dialogState, dialogTitle } = storeToRefs(personneStore);

const { canEditAdditionals } = usePersonne();

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

      <personne-dialog-manage-additional
        v-if="
          (dialogState == PersonneDialogState.ManageAdditional ||
            dialogState == PersonneDialogState.ManageAdditionalMultiple) &&
          structureTab == Tabs.SchoolStaff &&
          canEditAdditionals
        "
      />
    </v-card>
  </v-dialog>
</template>
