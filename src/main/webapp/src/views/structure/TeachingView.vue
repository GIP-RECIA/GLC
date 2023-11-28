<script setup lang="ts">
import SelectFilter from '@/components/filter/SelectFilter.vue';
import FilieresLayout from '@/components/layouts/FilieresLayout.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import { Etat } from '@/types/enums/Etat.ts';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { filterAccountStates, currentStructureConfig, isAdditional } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { teaching } = storeToRefs(fonctionStore);

const accountStates = computed<Array<Etat>>({
  get() {
    return currentStructureConfig.value ? currentStructureConfig.value.teachingStaff.accountStates : [];
  },
  set(states) {
    let config = currentStructureConfig.value!;
    config.teachingStaff.accountStates = states;
    currentStructureConfig.value = config;
  },
});

const isAdditionalTeachingMapping = false;
</script>

<template>
  <v-container fluid>
    <div class="d-flex justify-end mb-4 mb-sm-0">
      <v-btn
        v-if="isAdditionalTeachingMapping"
        variant="tonal"
        prepend-icon="fas fa-user-pen"
        class="d-none d-md-flex me-2"
        @click="isAdditional = true"
      >
        {{ t('button.manage') }}
      </v-btn>
      <div class="account-filter">
        <select-filter v-if="filterAccountStates" v-model="accountStates" :items="filterAccountStates" />
      </div>
    </div>
    <filieres-layout :filieres="teaching" :account-states="accountStates" />

    <div class="fab ma-4 d-md-none">
      <v-btn
        v-if="isAdditionalTeachingMapping"
        variant="tonal"
        size="x-large"
        icon="fas fa-user-pen"
        @click="isAdditional = true"
      />
    </div>
  </v-container>
</template>
