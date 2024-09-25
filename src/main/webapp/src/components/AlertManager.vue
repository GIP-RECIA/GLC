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
import { useConfigurationStore, useStructureStore } from '@/stores';
import type { Alert, Filiere } from '@/types';
import { storeToRefs } from 'pinia';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { isQuickAdd, requestAdd } = storeToRefs(configurationStore);

const structureStore = useStructureStore();
const { currentEtab, staff, fonction } = storeToRefs(structureStore);

const { t } = useI18n();

const getDiscipline = (filieres: Array<Filiere> | undefined, code: string): string | undefined => {
  const codes = code.split('-');
  if (!filieres) return undefined;
  return filieres
    .find((filiere) => filiere.codeFiliere == codes[0])
    ?.disciplines.find((discipline) => discipline.code == codes[1])?.disciplinePoste;
};

const formatedAlert = (alert: Alert): Alert & { class?: any; actions?: any } => {
  if (!alert.title) return alert;
  const data = alert.title.split('_');
  const discipline =
    getDiscipline(fonction.value?.filieres, data[1]) ?? getDiscipline(fonction.value?.customMapping?.filieres, data[1]);
  if (!discipline) return alert;
  const actions: any = {};
  if (alert.action && fonction.value?.customMapping) {
    if (alert.title) {
      const doAlert = () => {
        requestAdd.value = {
          i18n: t('additional.add', { discipline }),
          function: data[1],
          type: 'code',
          searchList: staff.value.school,
        };
        isQuickAdd.value = true;
      };
      actions.click = () => doAlert();
    }
  }

  return {
    ...alert,
    title: t('alert.minMax.title', { discipline, value: data[3] }, parseInt(data[3])),
    text: t('alert.minMax.text', { minMax: t(`alert.minMax.${data[0]}`), required: data[2] }),
    class: [{ clicable: alert.action && fonction.value?.customMapping }],
    actions,
  };
};
</script>

<template>
  <v-alert
    v-for="(alert, index) in currentEtab?.alerts.map(formatedAlert)"
    :key="index"
    v-bind="alert"
    variant="tonal"
    rounded="lg"
    @="alert.actions ?? {}"
  />
</template>

<style scoped lang="scss">
.clicable {
  cursor: pointer;
}
</style>
