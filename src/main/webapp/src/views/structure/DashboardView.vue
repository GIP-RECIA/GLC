<script setup lang="ts">
import AlertManager from '@/components/AlertManager.vue';
import CustomPagination from '@/components/CustomPagination.vue';
import PersonneCard from '@/components/PersonneCard.vue';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import { DashboardPanel } from '@/types/enums/DashboardPanel.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useDisplay } from 'vuetify';

const { t } = useI18n();

const personneStore = usePersonneStore();
const { deletingPersonnes, deletedPersonnes } = storeToRefs(personneStore);

const structureStore = useStructureStore();
const { currentEtab } = storeToRefs(structureStore);

const pageItems = ref<Array<SimplePersonne> | undefined>();
const pageItems2 = ref<Array<SimplePersonne> | undefined>();
const pageItems3 = ref<Array<SimplePersonne> | undefined>();

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
    <alert-manager />

    <v-expansion-panels v-model="panel">
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
                class="d-flex align-center pa-2"
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
                class="d-flex align-center pa-2"
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

      <v-expansion-panel :value="DashboardPanel.WithoutFunctions" :elevation="0" rounded="lg">
        <v-expansion-panel-title>
          <div class="expansion-title">
            {{
              `${t('withoutFunctions')} (${currentEtab?.withoutFunctions ? currentEtab.withoutFunctions.length : 0})`
            }}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row v-if="pageItems3 && pageItems3.length > 0">
            <transition-group>
              <v-col
                v-for="(personne, index) in pageItems3"
                :key="index"
                :cols="12"
                :sm="6"
                :md="4"
                :lg="3"
                :xxl="2"
                class="d-flex align-center pa-2"
              >
                <personne-card variant="tonal" :personne="personne" />
              </v-col>
            </transition-group>
          </v-row>
          <custom-pagination
            :items="currentEtab?.withoutFunctions"
            :items-per-page="itemsPerPage"
            hide-single-page
            class="mt-4"
            @update:page="(items) => (pageItems3 = items)"
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
