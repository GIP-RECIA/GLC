<script setup lang="ts">
import CustomPagination from '@/components/CustomPagination.vue';
import PersonneCard from '@/components/PersonneCard.vue';
import { usePersonneStore } from '@/stores/personneStore';
import type { SimplePersonne } from '@/types/personneType';
import { storeToRefs } from 'pinia';
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const personneStore = usePersonneStore();
const { deletedPersonnes } = storeToRefs(personneStore);

const pageItems = ref<Array<SimplePersonne> | undefined>();
</script>

<template>
  <v-container fluid>
    <v-card :subtitle="`${t('warning', 2)} (${0})`" flat class="mb-4">
      <v-card-text></v-card-text>
    </v-card>
    <v-card :subtitle="`${t('deletingOrDeletedAccounts')} (${deletedPersonnes.length})`" flat>
      <v-card-text>
        <v-row v-if="pageItems && pageItems.length > 0">
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
            <personne-card variant="tonal" :personne="personne" />
          </v-col>
        </v-row>
        <custom-pagination
          :items="deletedPersonnes"
          :items-per-page="20"
          hide-single-page
          class="mt-4"
          @update:page="(items) => (pageItems = items)"
        />
      </v-card-text>
    </v-card>
  </v-container>
</template>
