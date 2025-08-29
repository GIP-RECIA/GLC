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
import { storeToRefs } from 'pinia'
import { watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import AttachDialog from '@/components/dialogs/AttachDialog.vue'
import PersonneDialog from '@/components/dialogs/personne/PersonneDialog.vue'
import QuickAddDialog from '@/components/dialogs/QuickAddDialog.vue'
import AccountTab from '@/components/tabs/structure/AccountTab.vue'
import CategorieTab from '@/components/tabs/structure/CategorieTab.vue'
import DashboardTab from '@/components/tabs/structure/DashboardTab.vue'
import { useConfigurationStore, useStructureStore } from '@/stores'
import { Tabs } from '@/types/enums'

const isDev = import.meta.env.DEV

const configurationStore = useConfigurationStore()
const { structureTab, isAttach } = storeToRefs(configurationStore)

const structureStore = useStructureStore()
const { initCurrentEtab } = structureStore
const { fonction, filieresByStaff } = storeToRefs(structureStore)

const { t } = useI18n()
const route = useRoute()

watch(
  () => route.params.structureId,
  (newValue) => {
    if (typeof newValue !== 'undefined' && newValue !== null)
      initCurrentEtab(Number(newValue))
  },
  { immediate: true },
)
</script>

<template>
  <v-tabs
    v-model="structureTab"
    align-tabs="center"
    show-arrows
    hide-slider
    selected-class="slide-group-item--activate"
    class="mt-2"
  >
    <v-tab :value="Tabs.Dashboard" tabindex="0">
      {{ t('tab.dashboard') }}
    </v-tab>
    <v-tab v-if="filieresByStaff.teaching" :value="Tabs.Teaching">
      {{ t('tab.teaching') }}
    </v-tab>
    <v-tab v-if="filieresByStaff.school" :value="Tabs.School">
      {{ t('tab.school') }}
    </v-tab>
    <v-tab v-if="filieresByStaff.collectivity" :value="Tabs.Collectivity">
      {{ t('tab.collectivity') }}
    </v-tab>
    <v-tab v-if="filieresByStaff.academic" :value="Tabs.Academic">
      {{ t('tab.academic') }}
    </v-tab>
    <v-tab v-if="isDev" :value="Tabs.Accounts">
      {{ t('tab.accounts') }}
    </v-tab>
  </v-tabs>
  <v-window v-model="structureTab">
    <v-window-item :value="Tabs.Dashboard">
      <DashboardTab />
    </v-window-item>
    <v-window-item v-if="filieresByStaff.teaching" :value="Tabs.Teaching">
      <CategorieTab :categorie="Tabs.Teaching" />
    </v-window-item>
    <v-window-item v-if="filieresByStaff.school" :value="Tabs.School">
      <CategorieTab :categorie="Tabs.School">
        <template #actions>
          <v-btn
            v-if="fonction?.customMapping"
            variant="tonal"
            prepend-icon="fas fa-link"
            class="d-none d-sm-flex me-2 custom-height"
            @click="isAttach = true"
          >
            {{ t('button.attach') }}
          </v-btn>
        </template>
        <template #footer>
          <div class="fab ma-4 d-sm-none">
            <v-btn
              v-if="fonction?.customMapping"
              variant="tonal"
              size="x-large"
              icon="fas fa-link"
              @click="isAttach = true"
            />
          </div>
        </template>
      </CategorieTab>
    </v-window-item>
    <v-window-item v-if="filieresByStaff.collectivity" :value="Tabs.Collectivity">
      <CategorieTab :categorie="Tabs.Collectivity" />
    </v-window-item>
    <v-window-item v-if="filieresByStaff.academic" :value="Tabs.Academic">
      <CategorieTab :categorie="Tabs.Academic" />
    </v-window-item>
    <v-window-item v-if="isDev" :value="Tabs.Accounts">
      <AccountTab />
    </v-window-item>
  </v-window>
  <PersonneDialog />
  <AttachDialog />
  <QuickAddDialog />
</template>

<style scoped lang="scss">
.slide-group-item--activate {
  background-color: rgba(var(--v-theme-primary), var(--v-activated-opacity)) !important;
}
</style>
