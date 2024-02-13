<script setup lang="ts">
import PersonneChip from '@/components/search/personne/PersonneChip.vue';
import PersonneListItem from '@/components/search/personne/PersonneListItem.vue';
import { searchPersonne } from '@/services/personneService.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  searchList: Array<SimplePersonne> | undefined;
}>();

const emit = defineEmits<(event: 'update:select', payload: number | undefined) => void>();

const isLoading = ref<boolean>(false);
const isHideNoData = ref<boolean>(true);
const isSearchingOut = ref<boolean>(false);
const select = ref<SimplePersonne | undefined>();
const items = ref<Array<SimplePersonne>>([]);

const filterItems = (newSearch: string | undefined) => {
  if (typeof newSearch !== 'undefined' && newSearch !== null) {
    if (newSearch.length > 3 && !newSearch.includes('(')) {
      newSearch = newSearch
        .toLowerCase()
        .normalize('NFD')
        .replace(/\p{Diacritic}/gu, '');

      isLoading.value = true;
      isHideNoData.value = false;
      isSearchingOut.value ? findOutOfStructure(newSearch) : findInStructure(newSearch);
    } else {
      items.value = [];
      isLoading.value = false;
      isHideNoData.value = true;
    }
  } else items.value = [];
};

const findInStructure = (searchValue: string): void => {
  if (props.searchList) filterFromSource(props.searchList, searchValue);
  isLoading.value = false;
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
    }
  }
  isLoading.value = false;
};

const filterFromSource = (source: Array<SimplePersonne>, searchValue: string): void => {
  items.value = source.filter((personne) => {
    let filter = personne.cn.toLowerCase().indexOf(searchValue) > -1;
    if (personne.email) filter = filter || personne.email.toLowerCase().indexOf(searchValue) > -1;
    if (personne.uid) filter = filter || personne.uid.toLowerCase().indexOf(searchValue) > -1;

    return filter;
  });
};

watch(isSearchingOut, () => {
  if (select.value != undefined) {
    select.value = undefined;
    emit('update:select', undefined);
  }
});
</script>

<template>
  <div>
    <v-switch
      v-model="isSearchingOut"
      :label="t('searchOutOfStructure')"
      color="primary"
      density="compact"
      class="mb-2"
      hide-details
      inset
    />
    <v-autocomplete
      v-model="select"
      :label="t('people', 1)"
      :items="items"
      :custom-filter="() => true"
      :hide-no-data="isHideNoData || isLoading"
      no-data-text="search.noResults"
      :item-title="['cn']"
      :placeholder="t('search.personne.placeholder')"
      autofocus
      hide-details
      return-object
      clearable
      chips
      variant="outlined"
      @update:search="filterItems"
      @click:clear="items = []"
      @update:model-value="$emit('update:select', select?.id)"
    >
      <template #chip="{ props, item }">
        <personne-chip v-bind="props" :personne="item?.raw" />
      </template>
      <template #item="{ props, item }">
        <personne-list-item v-bind="props" :personne="item?.raw" />
      </template>
      <template #prepend-inner>
        <v-progress-circular v-show="isLoading" indeterminate />
      </template>
    </v-autocomplete>
  </div>
</template>