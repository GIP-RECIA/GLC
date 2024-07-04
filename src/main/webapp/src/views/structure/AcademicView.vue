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
import SelectFilter from '@/components/filter/SelectFilter.vue';
import FilieresLayout from '@/components/layouts/FilieresLayout.vue';
import PersonneSearch from '@/components/search/personne/PersonneSearch.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import type { Etat } from '@/types/enums/Etat.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';

const configurationStore = useConfigurationStore();
const { filterAccountStates, currentStructureConfig } = storeToRefs(configurationStore);

const structureStore = useStructureStore();
const { currentEtab, staff, filieresByStaff } = storeToRefs(structureStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const { currentPersonne } = storeToRefs(personneStore);

const accountStates = computed<Array<Etat>>({
  get() {
    return currentStructureConfig.value ? currentStructureConfig.value.academicStaff.accountStates : [];
  },
  set(states) {
    let config = currentStructureConfig.value!;
    config.academicStaff.accountStates = states;
    currentStructureConfig.value = config;
  },
});

const selectedUser = computed<SimplePersonne | undefined>({
  get() {
    return undefined;
  },
  set(user) {
    currentPersonne.value = undefined;
    if (user) initCurrentPersonne(user.id, true);
  },
});
</script>

<template>
  <v-container fluid>
    <div class="d-flex justify-end mb-4 mb-sm-0">
      <personne-search
        v-model="selectedUser"
        :search-list="staff.academic"
        search-type="IN"
        variant="solo"
        class="w-100 staff-search me-2"
      />
      <select-filter
        v-if="filterAccountStates"
        v-model="accountStates"
        :items="filterAccountStates"
        class="account-filter"
      />
    </div>
    <filieres-layout :filieres="filieresByStaff.academic" :account-states="accountStates" />
  </v-container>
</template>
