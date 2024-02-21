<script setup lang="ts">
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import type { Alert } from '@/types/alertType.ts';
import { storeToRefs } from 'pinia';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { isQuickAdd, requestAdd } = storeToRefs(configurationStore);

const structureStore = useStructureStore();
const { currentEtab, staff, fonction } = storeToRefs(structureStore);

const { t } = useI18n();

const doAlert = (alert: Alert): void => {
  if (alert.action && fonction.value?.customMapping) {
    if (alert.title) {
      requestAdd.value = {
        i18n: `additional.add.${alert.title}`,
        function: alert.title.replace('.', '-'),
        type: 'code',
        searchList: staff.value.school,
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
    variant="tonal"
    rounded="lg"
    :class="[alert.action && fonction?.customMapping ? 'clicable' : '', 'mb-4']"
    @click="doAlert(alert)"
  />
</template>

<style scoped lang="scss">
.clicable {
  cursor: pointer;
}
</style>
