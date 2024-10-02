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
import { useConfigurationStore, usePersonneStore, useStructureStore } from '@/stores';
import type { SimplePersonne } from '@/types';
import { Etat } from '@/types/enums';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { filterAccountStates, currentStructureConfig, isAttach } = storeToRefs(configurationStore);

const structureStore = useStructureStore();
const { staff, fonction, filieresByStaff } = storeToRefs(structureStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const { currentPersonne } = storeToRefs(personneStore);

const { t } = useI18n();

const accountStates = computed<Array<Etat>>({
  get() {
    return currentStructureConfig.value ? currentStructureConfig.value.schoolStaff.accountStates : [];
  },
  set(states) {
    let config = currentStructureConfig.value!;
    config.schoolStaff.accountStates = states;
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
    <div class="d-flex align-center justify-end mb-4 mb-sm-0">
      <v-btn
        v-if="fonction?.customMapping"
        variant="tonal"
        prepend-icon="fas fa-link"
        class="d-none d-sm-flex me-2 custom-height"
        @click="isAttach = true"
      >
        {{ t('button.attach') }}
      </v-btn>
      <personne-search
        v-model="selectedUser"
        :search-list="staff.school"
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
    <filieres-layout :filieres="filieresByStaff.school" :account-states="accountStates" />

    <div class="fab ma-4 d-sm-none">
      <v-btn
        v-if="fonction?.customMapping"
        variant="tonal"
        size="x-large"
        icon="fas fa-link"
        @click="isAttach = true"
      />
    </div>
  </v-container>
</template>
