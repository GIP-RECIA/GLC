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
import type { SearchStructure } from '@/types/index.ts'
import { storeToRefs } from 'pinia'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDisplay } from 'vuetify'
import CustomPagination from '@/components/old-ui/CustomPagination.vue'
import { useStructureStore } from '@/stores/index.ts'

const structureStore = useStructureStore()
structureStore.init()
const { etabs } = storeToRefs(structureStore)

const { t } = useI18n()
const { name } = useDisplay()

const search = ref<string | undefined>()

const pageItems = ref<SearchStructure[] | undefined>()

const itemsPerPage = computed<number>(() => {
  const defaultItemsPerPage = 10

  switch (name.value) {
    case 'xs':
      return defaultItemsPerPage
    case 'sm':
      return defaultItemsPerPage
    case 'md':
      return 2 * defaultItemsPerPage
    case 'lg':
      return 3 * defaultItemsPerPage
    case 'xl':
      return 3 * defaultItemsPerPage
    case 'xxl':
      return 4 * defaultItemsPerPage
    default:
      return defaultItemsPerPage
  }
})

const items = computed<SearchStructure[] | undefined>(() => {
  if (search.value !== undefined && search.value != null) {
    const searchValue = search.value
      .toLowerCase()
      .normalize('NFD')
      .replace(/\p{Diacritic}/gu, '')

    return etabs.value?.filter((etablissement) => {
      let filters = etablissement.nom.toLowerCase().includes(searchValue)
        || etablissement.siren.toString().includes(searchValue)
      if (etablissement.uai) {
        filters = filters || etablissement.uai.toLowerCase().includes(searchValue)
      }
      if (etablissement.ville) {
        filters = filters || etablissement.ville.toLowerCase().includes(searchValue)
      }

      return filters
    })
  }
  else {
    return etabs.value
  }
})
</script>

<template>
  <v-container>
    <v-text-field
      v-model="search"
      :placeholder="t('search.structure.placeholder')"
      variant="solo"
      rounded
      clearable
      hide-details
      class="mb-8"
    >
      <template #prepend-inner>
        <v-icon
          icon="fas fa-search"
          size="x-small"
          class="mx-1"
        />
      </template>
    </v-text-field>

    <div
      v-if="pageItems && pageItems.length > 0"
      class="structure-container"
    >
      <v-card
        v-for="etablissement in pageItems"
        :key="etablissement.id"
        :to="{
          name: 'structure',
          params: { structureId: etablissement.id },
        }"
        class="w-100"
      >
        <v-card-text>
          {{ etablissement.nom }}
          <div class="subtitle">
            {{
              etablissement.type
                ? `${etablissement.type} ${etablissement.uai}`
                : etablissement.uai
            }}
          </div>
        </v-card-text>
      </v-card>
    </div>
    <div
      v-else
      class="d-flex flex-column align-center justify-center pa-10"
    >
      <v-icon
        icon="fas fa-filter-circle-xmark"
        size="x-large"
      />
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
.structure-container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;
  // grid-auto-rows: 1fr;

  @media (width >= 768px) {
    grid-template-columns: 1fr 1fr;
  }

  @media (width >= 1280px) {
    grid-template-columns: 1fr 1fr 1fr;
  }

  @media (width >= 1920px) {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }

  @media (width >= 2560px) {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
  }
}

.subtitle {
  font-size: 0.775rem;
  font-weight: 400;
  opacity: var(--v-medium-emphasis-opacity);
}
</style>
