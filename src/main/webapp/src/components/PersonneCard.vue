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
import { usePersonneStore } from '@/stores/personneStore.ts';
import type { enumValues } from '@/types/enumValuesType.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { getEtat, getIcon } from '@/utils/accountUtils.ts';
import { format } from 'date-fns';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;

const { t } = useI18n();

const props = defineProps<{
  personne: SimplePersonne;
  variant?: NonNullable<'flat' | 'text' | 'elevated' | 'tonal' | 'outlined' | 'plain'>;
}>();

const data = computed<{ etat: enumValues; icon: string; tooltip: string }>(() => {
  const etat = getEtat(props.personne.etat);
  const icon = etat.icon ? etat.icon : getIcon(props.personne.source);
  const suppressDate = props.personne.dateSuppression && format(props.personne.dateSuppression, 'P');
  const supressString = suppressDate ? ` (${t('person.status.deletingDate', { suppressDate })})` : '';
  const tooltip = t(etat.i18n) + supressString;

  return { etat, icon, tooltip };
});
</script>

<template>
  <v-card :variant="variant" tag="button" class="w-100" @click="initCurrentPersonne(personne.id, true)">
    <v-card-text class="d-flex align-center text-left pa-3">
      <v-tooltip :text="data.tooltip" location="bottom start">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props" :icon="data.icon" :color="data.etat.color" class="me-2" />
        </template>
      </v-tooltip>
      {{ personne.cn }}
    </v-card-text>
  </v-card>
</template>
