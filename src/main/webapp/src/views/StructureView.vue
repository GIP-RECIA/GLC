<script setup lang="ts">
import AdditionalModal from "@/components/modals/AdditionalModal.vue";
import PersonneModal from "@/components/modals/PersonneModal.vue";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useStructureStore } from "@/stores/structureStore";
import { Tabs } from "@/types/enums/Tabs";
import AccountView from "@/views/structure/AccountView.vue";
import AdministrativeView from "@/views/structure/AdministrativeView.vue";
import DashboardView from "@/views/structure/DashboardView.vue";
import ExportView from "@/views/structure/ExportView.vue";
import InfoView from "@/views/structure/InfoView.vue";
import TeachingView from "@/views/structure/TeachingView.vue";
import { storeToRefs } from "pinia";
import { watch } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

const { t } = useI18n();

const route = useRoute();
const { structureId } = route.params;

const structureStore = useStructureStore();
structureStore.initCurrentEtab(Number(structureId));

const configurationStore = useConfigurationStore();
const { currentTab } = storeToRefs(configurationStore);

watch(
  () => route.params.structureId,
  (newValue) => {
    if (typeof newValue !== "undefined" && newValue !== null)
      structureStore.initCurrentEtab(Number(newValue));
  }
);
</script>

<template>
  <div>
    <v-tabs
      v-model="currentTab"
      align-tabs="center"
      show-arrows
      hide-slider
      selected-class="slide-group-item--activate"
      class="mt-2"
    >
      <v-tab :value="Tabs.Dashboard">{{ t("tabs.dashboard") }}</v-tab>
      <v-tab :value="Tabs.Info">{{ t("tabs.information") }}</v-tab>
      <v-tab :value="Tabs.AdministrativeStaff">{{
        t("tabs.administrativeStaff")
      }}</v-tab>
      <v-tab :value="Tabs.TeachingStaff">{{ t("tabs.teachingStaff") }}</v-tab>
      <v-tab :value="Tabs.Accounts">{{ t("tabs.accounts") }}</v-tab>
      <v-tab :value="Tabs.Exports">{{ t("tabs.exports") }}</v-tab>
    </v-tabs>
    <v-window v-model="currentTab">
      <v-window-item :value="Tabs.Dashboard">
        <dashboard-view />
      </v-window-item>
      <v-window-item :value="Tabs.Info">
        <info-view />
      </v-window-item>
      <v-window-item :value="Tabs.AdministrativeStaff">
        <administrative-view />
      </v-window-item>
      <v-window-item :value="Tabs.TeachingStaff">
        <teaching-view />
      </v-window-item>
      <v-window-item :value="Tabs.Accounts">
        <account-view />
      </v-window-item>
      <v-window-item :value="Tabs.Exports">
        <export-view />
      </v-window-item>
    </v-window>
    <personne-modal />
    <additional-modal />
  </div>
</template>

<style scoped lang="scss">
.slide-group-item--activate {
  background-color: rgba(
    var(--v-theme-primary),
    var(--v-activated-opacity)
  ) !important;
}
</style>
