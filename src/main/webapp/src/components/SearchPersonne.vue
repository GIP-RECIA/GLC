<script setup lang="ts">
import PersonneChip from "@/components/PersonneChip.vue";
import PersonneListItem from "@/components/PersonneListItem.vue";
import { searchPersonne } from "@/services/personneService";
import type { SearchPersonne } from "@/types/personneType";
import debounce from "lodash.debounce";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const props = defineProps<{
  searchList: Array<SearchPersonne> | undefined;
}>();

const emit =
  defineEmits<(event: "update:select", payload: number | undefined) => void>();

const select = ref<SearchPersonne | undefined>();
const loading = ref<boolean>(false);
const items = ref<Array<SearchPersonne>>([]);
const searchOutOfStructure = ref<boolean>(false);

watch(searchOutOfStructure, () => {
  if (select.value != undefined) {
    select.value = undefined;
    emit("update:select", undefined);
  }
});

const filterItems = (newSearch: string | undefined) => {
  if (typeof newSearch !== "undefined" && newSearch !== null) {
    if (newSearch.length > 3) {
      loading.value = true;
      searchOutOfStructure.value
        ? findOutOfStructure(newSearch)
        : findInStructure(newSearch);
    } else {
      items.value = [];
      loading.value = false;
    }
  } else items.value = [];
};

const findInStructure = (searchValue: string): void => {
  searchValue = searchValue.toLocaleLowerCase();
  if (props.searchList) {
    items.value = props.searchList
      .filter(
        (personne) =>
          personne.displayName.toLowerCase().indexOf(searchValue) > -1 ||
          personne.uid.toLowerCase().indexOf(searchValue) > -1
      )
      .map((personne) => {
        return {
          ...personne,
          searchValue: `${personne.displayName} (${personne.uid})`,
        };
      });
  }
  loading.value = false;
};

const findOutOfStructure = debounce(async (searchValue: string) => {
  try {
    items.value = (await searchPersonne(searchValue)).data;
  } catch (e) {
    console.error(e);
  }
  loading.value = false;
}, 500);
</script>

<template>
  <div>
    <v-switch
      v-model="searchOutOfStructure"
      :label="t('searchOutOfStructure')"
      hide-details
      inset
    />
    <v-autocomplete
      v-model="select"
      :label="t('people', 1)"
      :loading="loading"
      :items="items"
      item-title="searchValue"
      hide-no-data
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
    </v-autocomplete>
  </div>
</template>
