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
import CustomPagination from '@/components/CustomPagination.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import type { SimpleEtablissement } from '@/types/etablissementType.ts';
import { useSessionStorage } from '@vueuse/core';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useDisplay } from 'vuetify';

const configurationStore = useConfigurationStore();
const { search } = storeToRefs(configurationStore);

const structureStore = useStructureStore();
structureStore.init();
const { etabs } = storeToRefs(structureStore);

const { t } = useI18n();
const { name } = useDisplay();

const pageItems = ref<Array<SimpleEtablissement> | undefined>();

const itemsPerPage = computed<number>(() => {
  const defaultItemsPerPage = 10;

  switch (name.value) {
    case 'xs':
      return defaultItemsPerPage;
    case 'sm':
      return defaultItemsPerPage;
    case 'md':
      return 2 * defaultItemsPerPage;
    case 'lg':
      return 3 * defaultItemsPerPage;
    case 'xl':
      return 3 * defaultItemsPerPage;
    case 'xxl':
      return 4 * defaultItemsPerPage;
    default:
      return defaultItemsPerPage;
  }
});

const items = computed<Array<SimpleEtablissement> | undefined>(() => {
  if (search.value != undefined && search.value != null) {
    const searchValue = search.value
      .toLowerCase()
      .normalize('NFD')
      .replace(/\p{Diacritic}/gu, '');

    return etabs.value?.filter((etablissement) => {
      let filters =
        etablissement.nom.toLowerCase().indexOf(searchValue) > -1 ||
        etablissement.uai.toLowerCase().indexOf(searchValue) > -1 ||
        etablissement.siren.toString().indexOf(searchValue) > -1;
      if (etablissement.ville) {
        filters = filters || etablissement.ville.toLowerCase().indexOf(searchValue) > -1;
      }

      return filters;
    });
  } else return etabs.value;
});

const isInfo = useSessionStorage<boolean>(`${__APP_SLUG__}.is-info`, true);
</script>

<template>
  <v-container>
    <v-alert v-model="isInfo" type="info" variant="tonal" rounded="lg" class="mb-8" closable>
      Cette application vient compléter l'application de gestion des comptes en permettant de gérer plus finement les
      fonctions des personnels administratifs. Elle remplacera progressivement l'application historique de gestion des
      comptes de l'ENT.
    </v-alert>
    <v-text-field
      v-model="search"
      :placeholder="t('search.structure.placeholder')"
      variant="solo"
      rounded
      clearable
      flat
      hide-details
      class="mb-8"
    >
      <template #prepend-inner>
        <v-icon icon="fas fa-search" size="x-small" class="mx-1" />
      </template>
    </v-text-field>

    <v-row v-if="pageItems && pageItems.length > 0">
      <transition-group name="custom">
        <v-col
          v-for="(etablissement, index) in pageItems"
          :key="index"
          :cols="12"
          :md="6"
          :lg="4"
          :xxl="3"
          class="d-flex align-center pa-2"
        >
          <v-card
            :to="{
              name: 'structure',
              params: { structureId: etablissement.id },
            }"
            class="w-100"
            flat
          >
            <v-card-text class="card-info">
              <div class="title">
                {{ etablissement.nom }}
              </div>
              <div class="subtitle">
                {{ etablissement.type ? `${etablissement.type} ${etablissement.uai}` : etablissement.uai }}
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </transition-group>
    </v-row>
    <div v-else class="d-flex flex-column align-center justify-center pa-10">
      <v-icon icon="fas fa-filter-circle-xmark" size="x-large" />
      <div class="pt-2">{{ t('search.noResults') }}</div>
    </div>

    <custom-pagination
      :items="items"
      :items-per-page="itemsPerPage"
      hide-single-page
      class="mt-8"
      @update:page="(items) => (pageItems = items)"
    />
  </v-container>
</template>

<style scoped lang="scss">
.card-info {
  display: block;
  flex: none;
  letter-spacing: 0.0178571429em;
  overflow: hidden;
  text-overflow: ellipsis;
  text-transform: none;

  .subtitle {
    font-size: 0.775rem;
    font-weight: 400;
    opacity: var(--v-medium-emphasis-opacity);
  }
}
</style>
