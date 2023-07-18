<script setup lang="ts">
import PersonneCard from "@/components/PersonneCard.vue";
import { usePersonneStore } from "@/stores/personneStore";
import type { SimplePersonne } from "@/types/personneType";
import { storeToRefs } from "pinia";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const personneStore = usePersonneStore();
const { deletedPersonnes } = storeToRefs(personneStore);

const items = ref<Array<SimplePersonne> | undefined>();
const pageItems = ref<Array<SimplePersonne> | undefined>();
const pagination = ref({
  page: 1,
  pages: 1,
});
const itemsPerPage: number = 20;

watch(deletedPersonnes, (newValue) => {
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

items.value = deletedPersonnes.value;
</script>

<template>
  <v-container fluid>
    <v-card :subtitle="`${t('warning', 2)} (${0})`" flat class="mb-4">
      <v-card-text></v-card-text>
    </v-card>
    <v-card
      :subtitle="`${t('deletingOrDeletedAccounts')} (${
        deletedPersonnes.length
      })`"
      flat
    >
      <v-card-text v-if="pageItems && pageItems.length > 0">
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
            <personne-card variant="tonal" :personne="personne" />
          </v-col>
        </v-row>
        <v-pagination
          v-if="pagination.pages > 1"
          v-model="pagination.page"
          :length="pagination.pages"
          rounded="circle"
          class="mt-4"
          @update:model-value="showPage"
        />
      </v-card-text>
      <v-card-text v-else></v-card-text>
    </v-card>
  </v-container>
</template>
