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
import type { SimplePersonne } from '@/types'
import { storeToRefs } from 'pinia'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDisplay } from 'vuetify'
import CustomPagination from '@/components/CustomPagination.vue'
import AccountFilter from '@/components/filter/AccountFilter.vue'
import PersonneCard from '@/components/PersonneCard.vue'
import { useStructureStore } from '@/stores'

const structureStore = useStructureStore()
const { personnes } = storeToRefs(structureStore)

const { t } = useI18n()
const { name } = useDisplay()

const items = ref<Array<SimplePersonne> | undefined>()
const pageItems = ref<Array<SimplePersonne> | undefined>()

const itemsPerPage = computed<number>(() => {
  const defaultItemsPerPage = 10

  switch (name.value) {
    case 'xs':
      return defaultItemsPerPage
    case 'sm':
      return 2 * defaultItemsPerPage
    case 'md':
      return 3 * defaultItemsPerPage
    case 'lg':
      return 4 * defaultItemsPerPage
    case 'xl':
      return 4 * defaultItemsPerPage
    case 'xxl':
      return 6 * defaultItemsPerPage
    default:
      return defaultItemsPerPage
  }
})
</script>

<template>
  <v-container fluid>
    <AccountFilter
      class="mb-8"
      :search-list="personnes"
      @update:result="(result: Array<SimplePersonne>) => (items = result)"
    />
    <div v-if="pageItems && pageItems.length > 0" class="container">
      <PersonneCard v-for="personne in pageItems" :key="personne.id" variant="flat" :personne="personne" />
    </div>
    <div v-else class="d-flex flex-column align-center justify-center pa-10">
      <v-icon icon="fas fa-filter-circle-xmark" size="x-large" />
      <div class="pt-2">
        {{ t('search.noResults') }}
      </div>
    </div>
    <CustomPagination
      :items="items"
      :items-per-page="itemsPerPage"
      hide-single-page
      class="mt-8"
      @update:page="(items) => (pageItems = items)"
    />
  </v-container>
</template>

<style scoped lang="scss">
.container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;
  // grid-auto-rows: 1fr;

  @media (width >= 768px) {
    grid-template-columns: 1fr 1fr;
  }

  @media (width >= 960px) {
    grid-template-columns: 1fr 1fr 1fr;
  }

  @media (width >= 1280px) {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }

  @media (width >= 2560px) {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
  }
}
</style>
