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
const { currentEtab } = storeToRefs(structureStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const { currentPersonne, collectivityStaffList } = storeToRefs(personneStore);

const accountStates = computed<Array<Etat>>({
  get() {
    return currentStructureConfig.value ? currentStructureConfig.value.collectivityStaff.accountStates : [];
  },
  set(states) {
    let config = currentStructureConfig.value!;
    config.collectivityStaff.accountStates = states;
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
        :search-list="collectivityStaffList"
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
    <filieres-layout
      :filieres="currentEtab?.collectivityStaff"
      :without-functions="currentEtab?.withoutFunctionsCollectivity"
      :account-states="accountStates"
    />
  </v-container>
</template>
