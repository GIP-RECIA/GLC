<script setup lang="ts">
import SelectFilter from '@/components/filter/SelectFilter.vue';
import FilieresLayout from '@/components/layouts/FilieresLayout.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import type { Etat } from '@/types/enums/Etat.ts';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';

const configurationStore = useConfigurationStore();
const { filterAccountStates, currentStructureConfig } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
// const {} = storeToRefs(fonctionStore);

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
</script>

<template>
  <v-container fluid>
    <div class="d-flex justify-end mb-4 mb-sm-0">
      <div class="account-filter">
        <select-filter v-if="filterAccountStates" v-model="accountStates" :items="filterAccountStates" />
      </div>
    </div>
    <filieres-layout :filieres="[]" :account-states="accountStates" />
  </v-container>
</template>
