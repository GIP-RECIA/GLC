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
import { ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { searchPersonne } from '@/services/api'
import { errorHandler } from '@/utils'
import PersonneChip from './PersonneChip.vue'
import PersonneListItem from './PersonneListItem.vue'

const props = defineProps<{
  searchList?: Array<SimplePersonne>
  searchType?: 'IN' | 'OUT'
  variant?: 'outlined' | 'plain' | 'filled' | 'underlined' | 'solo' | 'solo-inverted' | 'solo-filled'
  chips?: boolean
}>()

const { t } = useI18n()

const modelValue = defineModel<SimplePersonne | undefined>()

const isLoading = ref<boolean>(false)
const isHideNoData = ref<boolean>(true)
const isSearchingOut = ref<boolean>(props.searchType === 'OUT')
const items = ref<Array<SimplePersonne>>([])

function filterItems(newSearch: string | undefined): void {
  if (newSearch && newSearch.length > 3 && !newSearch.includes('(')) {
    newSearch = newSearch
      .toLowerCase()
      .normalize('NFD')
      .replace(/\p{Diacritic}/gu, '')

    isLoading.value = true
    isHideNoData.value = false
    isSearchingOut.value ? findOutOfStructure(newSearch) : findInStructure(newSearch)
  }
  else {
    items.value = []
    isLoading.value = false
    isHideNoData.value = true
  }
}

function findInStructure(searchValue: string): void {
  if (props.searchList)
    filterFromSource(props.searchList, searchValue)
}

const out: { request?: string, response: Array<SimplePersonne> } = {
  request: undefined,
  response: [],
}

async function findOutOfStructure(searchValue: string): Promise<void> {
  if (out.request && searchValue.startsWith(out.request)) {
    filterFromSource(out.response, searchValue)
  }
  else {
    out.request = searchValue
    try {
      const response = await searchPersonne(searchValue)
      filterFromSource(response.data, searchValue)
      out.response = items.value
    }
    catch (e) {
      errorHandler(e)
      isLoading.value = false
    }
  }
}

function filterFromSource(source: Array<SimplePersonne>, searchValue: string): void {
  items.value = source.filter((personne) => {
    let filter = personne.cn.toLowerCase().includes(searchValue)
    if (personne.email)
      filter = filter || personne.email.toLowerCase().includes(searchValue)
    if (personne.uid)
      filter = filter || personne.uid.toLowerCase().includes(searchValue)

    return filter
  })
  isLoading.value = false
}
</script>

<template>
  <div>
    <v-switch
      v-if="props.searchType === undefined"
      v-model="isSearchingOut"
      :label="t('searchOutOfStructure')"
      color="primary"
      density="compact"
      class="mb-2"
      hide-details
      inset
    />
    <v-autocomplete
      v-model="modelValue"
      :items="items"
      :custom-filter="() => true"
      :hide-no-data="isHideNoData || isLoading"
      no-data-text="search.noResults"
      item-title="id"
      :placeholder="t('search.personne.placeholder')"
      density="compact"
      :variant="variant"
      rounded="xl"
      menu-icon=""
      prepend-inner-icon="fas fa-magnifying-glass"
      :chips="chips"
      clearable
      hide-details
      return-object
      flat
      @update:search="filterItems"
    >
      <template #chip="{ props: chipProps, item }">
        <PersonneChip v-bind="chipProps" :personne="item?.raw" />
      </template>
      <template #item="{ props: itemProps, item }">
        <PersonneListItem v-bind="itemProps" :personne="item?.raw" />
      </template>
      <template #append-inner>
        <v-progress-circular v-show="isLoading" indeterminate size="small" />
      </template>
    </v-autocomplete>
  </div>
</template>
