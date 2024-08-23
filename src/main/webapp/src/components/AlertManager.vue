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
import { useConfigurationStore, useStructureStore } from '@/stores/index.ts';
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
    :class="[{ clicable: alert.action && fonction?.customMapping }, 'mb-4']"
    @click="doAlert(alert)"
  />
</template>

<style scoped lang="scss">
.clicable {
  cursor: pointer;
}
</style>
