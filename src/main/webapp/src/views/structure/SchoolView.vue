<script setup lang="ts">
import SelectFilter from '@/components/filter/SelectFilter.vue';
import FilieresLayout from '@/components/layouts/FilieresLayout.vue';
import PersonneSearch from '@/components/search/personne/PersonneSearch.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import { Etat } from '@/types/enums/Etat.ts';
import type { SimplePersonne } from '@/types/personneType';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { filterAccountStates, currentStructureConfig, isPersonneSearch } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { isCustomMapping } = storeToRefs(fonctionStore);

const structureStore = useStructureStore();
const { currentEtab } = storeToRefs(structureStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const { currentPersonne, administrativeList } = storeToRefs(personneStore);

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
        v-if="isCustomMapping"
        variant="tonal"
        prepend-icon="fas fa-link"
        class="d-none d-sm-flex me-2 custom-height"
        @click="isPersonneSearch = true"
      >
        {{ t('button.attach') }}
      </v-btn>
      <personne-search
        v-model="selectedUser"
        :search-list="administrativeList"
        search-type="IN"
        variant="solo"
        class="w-100 max-width me-2"
      />
      <select-filter
        v-if="filterAccountStates"
        v-model="accountStates"
        :items="filterAccountStates"
        class="account-filter"
      />
    </div>
    <filieres-layout
      :filieres="currentEtab?.schoolStaff"
      :without-functions="currentEtab?.withoutFunctionsSchool"
      :account-states="accountStates"
    />

    <div class="fab ma-4 d-sm-none">
      <v-btn
        v-if="isCustomMapping"
        variant="tonal"
        size="x-large"
        icon="fas fa-link"
        @click="isPersonneSearch = true"
      />
    </div>
  </v-container>
</template>

<style scoped lang="scss">
.max-width {
  max-width: 300px;
}
</style>
