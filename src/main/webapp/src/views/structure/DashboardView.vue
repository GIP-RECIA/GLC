<script setup lang="ts">
import CustomPagination from '@/components/CustomPagination.vue';
import PersonneCard from '@/components/PersonneCard.vue';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { DashboardPanel } from '@/types/enums/DashboardPanel.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useDisplay } from 'vuetify';

const { t } = useI18n();

const personneStore = usePersonneStore();
const { deletingPersonnes, deletedPersonnes } = storeToRefs(personneStore);

const pageItems = ref<Array<SimplePersonne> | undefined>();
const pageItems2 = ref<Array<SimplePersonne> | undefined>();

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

const panel = ref<Array<DashboardPanel>>([DashboardPanel.DeletingAccounts]);
</script>

<template>
  <v-container fluid>
    <v-card :subtitle="`${t('warning', 2)} (${0})`" flat class="mb-4">
      <v-card-text></v-card-text>
    </v-card>

    <v-expansion-panels v-model="panel" mandatory>
      <v-expansion-panel :value="DashboardPanel.DeletingAccounts" :elevation="0" rounded="lg">
        <v-expansion-panel-title>
          <div class="expansion-title">
            {{ `${t('deletingAccounts')} (${deletingPersonnes.length})` }}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row v-if="pageItems && pageItems.length > 0">
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
                <personne-card variant="tonal" :personne="personne" />
              </v-col>
            </transition-group>
          </v-row>
          <custom-pagination
            :items="deletingPersonnes"
            :items-per-page="itemsPerPage"
            hide-single-page
            class="mt-4"
            @update:page="(items) => (pageItems = items)"
          />
        </v-expansion-panel-text>
      </v-expansion-panel>

      <v-expansion-panel :value="DashboardPanel.DeletedAccounts" :elevation="0" rounded="lg">
        <v-expansion-panel-title>
          <div class="expansion-title">
            {{ `${t('deletedAccounts')} (${deletedPersonnes.length})` }}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row v-if="pageItems2 && pageItems2.length > 0">
            <transition-group>
              <v-col
                v-for="(personne, index) in pageItems2"
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
            </transition-group>
          </v-row>
          <custom-pagination
            :items="deletedPersonnes"
            :items-per-page="itemsPerPage"
            hide-single-page
            class="mt-4"
            @update:page="(items) => (pageItems2 = items)"
          />
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-container>
</template>

<style scoped lang="scss">
.expansion-title {
  font-size: 0.875rem;
  font-weight: 400;
  letter-spacing: 0.0178571429em;
  opacity: var(--v-medium-emphasis-opacity);
}
</style>
<style lang="scss">
.v-expansion-panel-title__overlay {
  opacity: 0 !important;
}

.v-expansion-panel-text__wrapper {
  padding-left: 16px !important;
  padding-right: 16px !important;
}
</style>
