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
import AttachDialog from '@/components/dialogs/AttachDialog.vue';
import QuickAddDialog from '@/components/dialogs/QuickAddDialog.vue';
import PersonneDialog from '@/components/dialogs/personne/PersonneDialog.vue';
import AcademicTab from '@/components/tabs/structure/AcademicTab.vue';
import AccountTab from '@/components/tabs/structure/AccountTab.vue';
import CollectivityTab from '@/components/tabs/structure/CollectivityTab.vue';
import DashboardTab from '@/components/tabs/structure/DashboardTab.vue';
import SchoolTab from '@/components/tabs/structure/SchoolTab.vue';
import TeachingTab from '@/components/tabs/structure/TeachingTab.vue';
import { useConfigurationStore, useStructureStore } from '@/stores';
import { Tabs } from '@/types/enums';
import { storeToRefs } from 'pinia';
import { watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';

const isDev = import.meta.env.DEV;

const structureStore = useStructureStore();
const { initCurrentEtab } = structureStore;
const { filieresByStaff } = storeToRefs(structureStore);

const configurationStore = useConfigurationStore();
const { structureTab } = storeToRefs(configurationStore);

const { t } = useI18n();
const route = useRoute();

watch(
  () => route.params.structureId,
  (newValue) => {
    if (typeof newValue !== 'undefined' && newValue !== null) initCurrentEtab(Number(newValue));
  },
  { immediate: true },
);
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
    <v-tab :value="Tabs.Dashboard" tabindex="0">{{ t('tab.dashboard') }}</v-tab>
    <v-tab v-if="filieresByStaff.teaching" :value="Tabs.TeachingStaff">{{ t('tab.teachingStaff') }}</v-tab>
    <v-tab v-if="filieresByStaff.school" :value="Tabs.SchoolStaff">{{ t('tab.schoolStaff') }}</v-tab>
    <v-tab v-if="filieresByStaff.collectivity" :value="Tabs.CollectivityStaff">{{ t('tab.collectivityStaff') }}</v-tab>
    <v-tab v-if="filieresByStaff.academic" :value="Tabs.AcademicStaff">{{ t('tab.academicStaff') }}</v-tab>
    <v-tab v-if="isDev" :value="Tabs.Accounts">{{ t('tab.accounts') }}</v-tab>
  </v-tabs>
  <v-window v-model="structureTab">
    <v-window-item :value="Tabs.Dashboard">
      <dashboard-tab />
    </v-window-item>
    <v-window-item v-if="filieresByStaff.teaching" :value="Tabs.TeachingStaff">
      <teaching-tab />
    </v-window-item>
    <v-window-item v-if="filieresByStaff.school" :value="Tabs.SchoolStaff">
      <school-tab />
    </v-window-item>
    <v-window-item v-if="filieresByStaff.collectivity" :value="Tabs.CollectivityStaff">
      <collectivity-tab />
    </v-window-item>
    <v-window-item v-if="filieresByStaff.academic" :value="Tabs.AcademicStaff">
      <academic-tab />
    </v-window-item>
    <v-window-item v-if="isDev" :value="Tabs.Accounts">
      <account-tab />
    </v-window-item>
  </v-window>
  <personne-dialog />
  <attach-dialog />
  <quick-add-dialog />
</template>

<style scoped lang="scss">
.slide-group-item--activate {
  background-color: rgba(var(--v-theme-primary), var(--v-activated-opacity)) !important;
}
</style>
