<script setup lang="ts">
import PersonneListItem from '@/components/search/personne/PersonneListItem.vue';
import { searchPersonne } from '@/services/personneService.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const modelValue = defineModel<SimplePersonne | undefined>();

const isLoading = ref<boolean>(false);
const isHideNoData = ref<boolean>(true);
const items = ref<Array<SimplePersonne>>([]);

const filterItems = (newSearch: string | undefined) => {
  if (newSearch && newSearch.length > 3 && !newSearch.includes('(')) {
    newSearch = newSearch
      .toLowerCase()
      .normalize('NFD')
      .replace(/\p{Diacritic}/gu, '');

    isLoading.value = true;
    isHideNoData.value = false;
    findOutOfStructure(newSearch);
  } else {
    items.value = [];
    isLoading.value = false;
    isHideNoData.value = true;
  }
};

let out: { request?: string; response: Array<SimplePersonne> } = {
  request: undefined,
  response: [],
};

const findOutOfStructure = async (searchValue: string) => {
  if (out.request && searchValue.startsWith(out.request)) filterFromSource(out.response, searchValue);
  else {
    out.request = searchValue;
    try {
      const response = await searchPersonne(searchValue);
      filterFromSource(response.data, searchValue);
      out.response = items.value;
    } catch (e) {
      errorHandler(e);
      isLoading.value = false;
    }
  }
};

const filterFromSource = (source: Array<SimplePersonne>, searchValue: string): void => {
  items.value = source.filter((personne) => {
    let filter = personne.cn.toLowerCase().indexOf(searchValue) > -1;
    if (personne.email) filter = filter || personne.email.toLowerCase().indexOf(searchValue) > -1;
    if (personne.uid) filter = filter || personne.uid.toLowerCase().indexOf(searchValue) > -1;

    return filter;
  });
  isLoading.value = false;
};
</script>

<template>
  <v-autocomplete
    v-model="modelValue"
    :items="items"
    :custom-filter="() => true"
    :hide-no-data="isHideNoData || isLoading"
    no-data-text="search.noResults"
    :item-title="['cn']"
    :placeholder="t('search.personne.placeholder')"
    density="compact"
    variant="outlined"
    rounded="xl"
    menu-icon=""
    prepend-inner-icon="fas fa-magnifying-glass"
    hide-details
    return-object
    flat
    @update:search="filterItems"
  >
    <template #item="{ props, item }">
      <personne-list-item v-bind="props" :personne="item?.raw" />
    </template>
    <template #append-inner>
      <v-progress-circular v-show="isLoading" indeterminate size="small" />
    </template>
  </v-autocomplete>
</template>
