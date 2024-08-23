<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import AlertManager from '@/components/AlertManager.vue';
import CustomPagination from '@/components/CustomPagination.vue';
import PersonneCard from '@/components/PersonneCard.vue';
import InfoGrid from '@/components/info/InfoGrid.vue';
import { useStructureStore } from '@/stores/index.ts';
import { DashboardPanel } from '@/types/enums/DashboardPanel.ts';
import { Etat } from '@/types/enums/Etat.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { storeToRefs } from 'pinia';
import { computed, ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useDisplay } from 'vuetify';

const isDev = import.meta.env.DEV;

const structureStore = useStructureStore();
const { currentEtab, personnesByEtat } = storeToRefs(structureStore);

const { t } = useI18n();
const { name } = useDisplay();

const pageItems = ref<Array<SimplePersonne> | undefined>();
const pageItems2 = ref<Array<SimplePersonne> | undefined>();
const pageItems3 = ref<Array<SimplePersonne> | undefined>();

const itemsPerPage = computed<number>(() => {
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

    <info-grid v-if="isDev" class="mb-4" />

    <v-expansion-panels v-model="panel">
      <v-expansion-panel :value="DashboardPanel.DeletingAccounts" :elevation="0" rounded="lg">
        <v-expansion-panel-title>
          <div class="expansion-title">
            {{ `${t('deletingAccounts')} (${personnesByEtat.get(Etat.Deleting)?.length ?? 0})` }}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row v-if="pageItems && pageItems.length > 0">
            <transition-group name="custom">
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
            :items="personnesByEtat.get(Etat.Deleting)"
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
            {{ `${t('deletedAccounts')} (${personnesByEtat.get(Etat.Delete)?.length ?? 0})` }}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <v-row v-if="pageItems2 && pageItems2.length > 0">
            <transition-group name="custom">
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
            :items="personnesByEtat.get(Etat.Delete)"
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
            <transition-group name="custom">
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
