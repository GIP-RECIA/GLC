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
const { teaching } = storeToRefs(fonctionStore);

const accountStates = ref<Array<string>>([Etat.Valide]);
</script>

<template>
  <v-container fluid>
    <div class="d-flex justify-end mb-4 mb-sm-0">
      <select-filter v-if="filterAccountStates" v-model="accountStates" :items="filterAccountStates" />
    </div>
    <filieres-layout :filieres="teaching" :account-states="accountStates" />

    <div class="fab ma-4">
      <v-btn variant="tonal" icon="fas fa-user-pen" @click="isAdditional = true" />
    </div>
  </v-container>
</template>
