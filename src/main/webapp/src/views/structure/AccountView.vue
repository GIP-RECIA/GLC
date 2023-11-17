<script setup lang="ts">
import CustomPagination from '@/components/CustomPagination.vue';
import PersonneCard from '@/components/PersonneCard.vue';
import AccountFilter from '@/components/filter/AccountFilter.vue';
import { usePersonneStore } from '@/stores/personneStore.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { storeToRefs } from 'pinia';
import { computed, ref, watch } from 'vue';
import { useDisplay } from 'vuetify';

const personneStore = usePersonneStore();
const { personnes } = storeToRefs(personneStore);

const items = ref<Array<SimplePersonne> | undefined>();
const pageItems = ref<Array<SimplePersonne> | undefined>();

const itemsPerPage = computed<number>(() => {
  const { name } = useDisplay();
  const defaultItemsPerPage = 10;

  switch (name.value) {
    case 'xs':
      return defaultItemsPerPage;
    case 'sm':
      return 2 * defaultItemsPerPage;
    case 'md':
      return 3 * defaultItemsPerPage;
    case 'lg':
      return 4 * defaultItemsPerPage;
    case 'xl':
      return 4 * defaultItemsPerPage;
    case 'xxl':
      return 6 * defaultItemsPerPage;
    default:
      return defaultItemsPerPage;
  }
});

watch(personnes, (newValue) => {
  if (typeof newValue !== 'undefined' && newValue !== null) items.value = newValue;
});
</script>

<template>
  <v-container fluid>
    <account-filter
      class="mb-8"
      :search-list="personnes"
      @update:result="(result: Array<SimplePersonne>) => (items = result)"
    />
    <v-row>
      <transition-group>
        <v-col
          v-for="(personne, index) in pageItems"
          :key="index"
          :cols="12"
          :sm="6"
          :md="4"
          :lg="3"
          :xxl="2"
          class="pa-2"
        >
          <personne-card variant="flat" :personne="personne" />
        </v-col>
      </transition-group>
    </v-row>
    <custom-pagination
      :items="items"
      :items-per-page="itemsPerPage"
      hide-single-page
      class="mt-8"
      @update:page="(items) => (pageItems = items)"
    />
  </v-container>
</template>
