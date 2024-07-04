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
import PersonneCard from '@/components/PersonneCard.vue';
import AccountFilter from '@/components/filter/AccountFilter.vue';
import { useStructureStore } from '@/stores/structureStore.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useDisplay } from 'vuetify';

const structureStore = useStructureStore();
const { personnes } = storeToRefs(structureStore);

const { t } = useI18n();
const { name } = useDisplay();

const items = ref<Array<SimplePersonne> | undefined>();
const pageItems = ref<Array<SimplePersonne> | undefined>();

const itemsPerPage = computed<number>(() => {
  const defaultItemsPerPage = 10;

  switch (name.value) {
    case 'xs':
      return defaultItemsPerPage;
    case 'sm':
      return 2 * defaultItemsPerPage;
    case 'md':
      return 3 * defaultItemsPerPage;
    case 'lg':
      return 4 * defaultItemsPerPage;
    case 'xl':
      return 4 * defaultItemsPerPage;
    case 'xxl':
      return 6 * defaultItemsPerPage;
    default:
      return defaultItemsPerPage;
  }
});
</script>

<template>
  <v-container fluid>
    <account-filter
      class="mb-8"
      :search-list="personnes"
      @update:result="(result: Array<SimplePersonne>) => (items = result)"
    />
    <v-row v-if="pageItems && pageItems.length > 0" class="px-1">
      <transition-group name="custom">
        <v-col
          v-for="(personne, index) in pageItems"
          :key="index"
          :cols="12"
          :sm="6"
          :md="4"
          :lg="3"
          :xxl="2"
          class="d-flex align-center pa-2"
        >
          <personne-card variant="flat" :personne="personne" />
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
