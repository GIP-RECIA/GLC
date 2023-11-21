<script setup lang="ts">
import SelectFilter from '@/components/filter/SelectFilter.vue';
import FilieresLayout from '@/components/layouts/FilieresLayout.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import { Etat } from '@/types/enums/Etat';
import { storeToRefs } from 'pinia';
import { ref } from 'vue';

const configurationStore = useConfigurationStore();
const { filterAccountStates, isAdditional } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { administrative, isCustomMapping } = storeToRefs(fonctionStore);

const accountStates = ref<Array<string>>([Etat.Valide]);
</script>

<template>
  <v-container fluid>
    <div class="d-flex align-center justify-end mb-4 mb-sm-0">
      <div class="account-filter">
        <select-filter v-if="filterAccountStates" v-model="accountStates" :items="filterAccountStates" />
      </div>
    </div>
    <filieres-layout :filieres="administrative" :account-states="accountStates" />

    <div class="fab ma-4">
      <v-btn v-if="isCustomMapping" variant="tonal" icon="fas fa-user-pen" @click="isAdditional = true" />
    </div>
  </v-container>
</template>
