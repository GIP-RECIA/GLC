<script setup lang="ts">
import PersonneCard from "@/components/PersonneCard.vue";
import AccountFilter from "@/components/filter/AccountFilter.vue";
import { usePersonneStore } from "@/stores/personneStore";
import type { SearchPersonne } from "@/types/personneType";
import { storeToRefs } from "pinia";
import { watch, ref } from "vue";

const personneStore = usePersonneStore();
const { searchList } = storeToRefs(personneStore);

const items = ref<Array<SearchPersonne> | undefined>();
const pageItems = ref<Array<SearchPersonne> | undefined>();
const pagination = ref({
  page: 1,
  pages: 1,
});
const itemsPerPage: number = 20;

watch(searchList, (newValue) => {
  if (typeof newValue !== "undefined" && newValue !== null)
    items.value = newValue;
});

watch(items, (newValue) => {
  if (typeof newValue !== "undefined" && newValue !== null) {
    pagination.value.page = 1;
    pagination.value.pages = Math.round(
      items.value ? items.value.length / itemsPerPage : 1
    );
    showPage(1);
  }
});

const showPage = (page: number) => {
  if (typeof items.value !== "undefined" && items.value !== null) {
    pageItems.value = items.value.filter((_, index) => {
      return page == 1
        ? index < itemsPerPage
        : index >= (page - 1) * itemsPerPage && index < page * itemsPerPage;
    });
  }
};

items.value = searchList.value;
</script>

<template>
  <v-container fluid>
    <account-filter
      class="mb-8"
      @update:result="(result: Array<SearchPersonne>) => items = result"
    />
    <v-row>
      <v-col
        v-for="(personne, index) in pageItems"
        :key="index"
        :cols="12"
        :md="6"
        :lg="4"
        :xxl="3"
        class="pa-2"
      >
        <personne-card variant="flat" :personne="personne" />
      </v-col>
    </v-row>
    <v-pagination
      v-if="pagination.pages > 1"
      v-model="pagination.page"
      :length="pagination.pages"
      rounded="circle"
      class="mt-8"
      @update:model-value="showPage"
    />
  </v-container>
</template>
