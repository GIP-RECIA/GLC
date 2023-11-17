<script setup lang="ts">
import FilieresLayout from '@/components/layouts/FilieresLayout.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import isEmpty from 'lodash.isempty';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { isAdditional } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { teaching } = storeToRefs(fonctionStore);

const showAll = ref<boolean>(false);
const isEmptyDisciplines = computed<boolean>(() => {
  return (
    teaching.value != undefined &&
    teaching.value.filter(
      (filiere) => filiere.disciplines.filter((discipline) => isEmpty(discipline.personnes)).length > 0,
    ).length > 0
  );
});
</script>

<template>
  <v-container fluid>
    <div class="d-flex justify-end mb-4 mb-sm-0">
      <v-btn
        v-if="isEmptyDisciplines"
        variant="tonal"
        :prepend-icon="showAll ? 'fas fa-eye-slash' : 'fas fa-eye'"
        :text="t(showAll ? 'button.hideEmpty' : 'button.showAll')"
        @click="showAll = !showAll"
      />
    </div>
    <filieres-layout :filieres="teaching" :show-all="showAll" />

    <div class="fab ma-4">
      <v-btn variant="tonal" icon="fas fa-user-pen" @click="isAdditional = true" />
    </div>
  </v-container>
</template>
