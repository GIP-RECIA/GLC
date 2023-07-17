<script setup lang="ts">
import { searchPersonne } from "@/services/personneService";
import type { SearchPersonne } from "@/types/personneType";
import debounce from "lodash.debounce";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const props = defineProps<{
  searchList: Array<SearchPersonne> | undefined;
}>();

defineEmits<(event: "update:select", payload: number | undefined) => void>();

const select = ref<{ id: number; name: string } | undefined>();
const search = ref<string>();
const loading = ref<boolean>(false);
const items = ref<Array<{ id: number; name: string }>>([]);
const searchOutOfStructure = ref<boolean>(false);

watch(search, (newSearch) => {
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
});

watch(searchOutOfStructure, () => {
  select.value = undefined;
});

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
          id: personne.id,
          name: `${personne.displayName} (${personne.uid})`,
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
      v-model:search="search"
      :label="t('people', 1)"
      :loading="loading"
      :items="items"
      item-title="name"
      hide-no-data
      hide-details
      return-object
      clearable
      variant="outlined"
      @click:clear="items = []"
      @update:model-value="$emit('update:select', select?.id)"
    />
  </div>
</template>
