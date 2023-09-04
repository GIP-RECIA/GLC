<script setup lang="ts">
import CustomPagination from '@/components/CustomPagination.vue';
import PersonneCard from '@/components/PersonneCard.vue';
import AccountFilter from '@/components/filter/AccountFilter.vue';
import { usePersonneStore } from '@/stores/personneStore';
import type { SimplePersonne } from '@/types/personneType';
import { storeToRefs } from 'pinia';
import { ref, watch } from 'vue';

const personneStore = usePersonneStore();
const { personnes } = storeToRefs(personneStore);

const items = ref<Array<SimplePersonne> | undefined>();
const pageItems = ref<Array<SimplePersonne> | undefined>();

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
    </v-row>
    <custom-pagination
      :items="items"
      :items-per-page="20"
      hide-single-page
      class="mt-8"
      @update:page="(items) => (pageItems = items)"
    />
  </v-container>
</template>
