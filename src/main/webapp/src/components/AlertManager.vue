<script setup lang="ts">
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import type { Alert } from '@/types/alertType.ts';
import { storeToRefs } from 'pinia';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { isQuickAdd, requestAdd } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { administrativeList } = storeToRefs(personneStore);

const structureStore = useStructureStore();
const { currentEtab } = storeToRefs(structureStore);

const doAlert = (alert: Alert): void => {
  if (alert.action) {
    if (alert.title) {
      requestAdd.value = {
        i18n: `additional.add.${alert.title}`,
        function: alert.title.replace('.', '-'),
        type: 'code',
        searchList: administrativeList.value,
      };
      isQuickAdd.value = true;
    }
  }
};
</script>

<template>
  <v-alert
    v-for="(alert, index) in currentEtab?.alerts"
    :key="index"
    :title="alert.title && t(`alert.title.${alert.title}`)"
    :text="alert.text && t(`alert.text.${alert.text}`)"
    :type="alert.type"
    rounded="lg"
    :class="[alert.action ? 'clicable' : '', 'mb-4']"
    @click="doAlert(alert)"
  />
</template>

<style scoped lang="scss">
.clicable {
  cursor: pointer;
}
</style>
