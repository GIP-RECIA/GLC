<script setup lang="ts">
import AdditionalDialog from '@/components/dialogs/AdditionalDialog.vue';
import QuickAddDialog from '@/components/dialogs/QuickAddDialog.vue';
import PersonneDialog from '@/components/dialogs/personne/PersonneDialog.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import AcademicView from '@/views/structure/AcademicView.vue';
import AccountView from '@/views/structure/AccountView.vue';
import CollectivityView from '@/views/structure/CollectivityView.vue';
import DashboardView from '@/views/structure/DashboardView.vue';
import InfoView from '@/views/structure/InfoView.vue';
import SchoolView from '@/views/structure/SchoolView.vue';
import TeachingView from '@/views/structure/TeachingView.vue';
import { storeToRefs } from 'pinia';
import { watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute } from 'vue-router';

const isDev = import.meta.env.DEV;

const structureStore = useStructureStore();
const { initCurrentEtab } = structureStore;

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
    <v-tab v-if="isDev" :value="Tabs.Info">{{ t('tab.information') }}</v-tab>
    <v-tab :value="Tabs.TeachingStaff">{{ t('tab.teachingStaff') }}</v-tab>
    <v-tab :value="Tabs.SchoolStaff">{{ t('tab.schoolStaff') }}</v-tab>
    <v-tab :value="Tabs.CollectivityStaff">{{ t('tab.collectivityStaff') }}</v-tab>
    <v-tab :value="Tabs.AcademicStaff">{{ t('tab.academicStaff') }}</v-tab>
    <v-tab v-if="isDev" :value="Tabs.Accounts">{{ t('tab.accounts') }}</v-tab>
  </v-tabs>
  <v-window v-model="structureTab">
    <v-window-item :value="Tabs.Dashboard" eager>
      <dashboard-view />
    </v-window-item>
    <v-window-item v-if="isDev" :value="Tabs.Info" eager>
      <info-view />
    </v-window-item>
    <v-window-item :value="Tabs.TeachingStaff" eager>
      <teaching-view />
    </v-window-item>
    <v-window-item :value="Tabs.SchoolStaff" eager>
      <school-view />
    </v-window-item>
    <v-window-item :value="Tabs.CollectivityStaff" eager>
      <collectivity-view />
    </v-window-item>
    <v-window-item :value="Tabs.AcademicStaff" eager>
      <academic-view />
    </v-window-item>
    <v-window-item v-if="isDev" :value="Tabs.Accounts" eager>
      <account-view />
    </v-window-item>
  </v-window>
  <personne-dialog />
  <additional-dialog />
  <quick-add-dialog />
</template>

<style scoped lang="scss">
.slide-group-item--activate {
  background-color: rgba(var(--v-theme-primary), var(--v-activated-opacity)) !important;
}
</style>
