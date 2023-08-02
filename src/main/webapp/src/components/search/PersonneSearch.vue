<script setup lang="ts">
import PersonneChip from "@/components/search/PersonneChip.vue";
import PersonneListItem from "@/components/search/PersonneListItem.vue";
import { searchPersonne } from "@/services/personneService";
import type { SimplePersonne } from "@/types/personneType";
import { errorHandler } from "@/utils/axiosUtils";
import debounce from "lodash.debounce";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const props = defineProps<{
  searchList: Array<SimplePersonne> | undefined;
}>();

const emit =
  defineEmits<(event: "update:select", payload: number | undefined) => void>();

const select = ref<SimplePersonne | undefined>();
const loading = ref<boolean>(false);
const items = ref<Array<SimplePersonne>>([]);
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
      .filter((personne) => {
        let filter = personne.cn.toLowerCase().indexOf(searchValue) > -1;

        if (personne.uid) {
          filter =
            filter || personne.uid.toLowerCase().indexOf(searchValue) > -1;
        }

        return filter;
      })
      .map((personne) => {
        return {
          ...personne,
          searchValue: personne.uid
            ? `${personne.cn} (${personne.uid})`
            : personne.cn,
        };
      });
  }
  loading.value = false;
};

const findOutOfStructure = debounce(async (searchValue: string) => {
  searchValue = searchValue.toLocaleLowerCase();
  try {
    const response = await searchPersonne(searchValue);
    items.value = response.data.map((personne: SimplePersonne) => {
      return {
        ...personne,
        searchValue: personne.uid
          ? `${personne.cn} (${personne.uid})`
          : personne.cn,
      };
    });
  } catch (e) {
    errorHandler(e);
  }
  loading.value = false;
}, 500);
</script>

<template>
  <div>
    <v-switch
      v-model="searchOutOfStructure"
      :label="t('searchOutOfStructure')"
      density="compact"
      class="mb-2"
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
