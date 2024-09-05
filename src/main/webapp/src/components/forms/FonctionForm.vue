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
import type { Discipline, Filiere } from '@/types';
import { filiereDisciplineToId, idToFonction, isBetween } from '@/utils';
import debounce from 'lodash.debounce';
import { computed, onMounted, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = withDefaults(
  defineProps<{
    filieres: Array<Filiere> | undefined;
    disabled?: Array<string>;
    disableFonctionEdit?: boolean;
    config?: {
      filiere?: Record<string, any>;
      discipline?: Record<string, any>;
      date?: Record<string, any>;
    };
  }>(),
  {
    filieres: undefined,
    disabled: undefined,
    config: () => ({
      filiere: {},
      discipline: {},
      disableFonctionEdit: false,
      date: {},
    }),
  },
);

const modelValue = defineModel<{ fonction: string; date: string }>();

type Form = {
  filiere: number | undefined;
  discipline: number | undefined;
  date: string | undefined;
};

const form = ref<Form>({
  filiere: undefined,
  discipline: undefined,
  date: undefined,
});

const isReady = ref<boolean>(false);

const filteredFilieres = computed<Array<Filiere> | undefined>(() => {
  if (!props.disabled) return props.filieres;
  return props.filieres
    ?.map((filiere) => {
      const disciplines: Array<Discipline> = filiere.disciplines.filter(
        (discipline) => !props.disabled?.includes(filiereDisciplineToId(filiere.id, discipline.id)),
      );

      return { ...filiere, disciplines };
    })
    .filter(({ disciplines }) => disciplines.length > 0);
});

const filteredDisciplines = computed<Array<Discipline> | undefined>(() => {
  return filteredFilieres.value?.find(({ id }) => id == form.value.filiere)?.disciplines;
});

/**
 * Update modelValue quand toutes les données sont replies
 */
watch(
  form,
  debounce((newValue: Form): void => {
    if (!isReady.value) return;
    const { filiere, discipline, date } = newValue;
    if (!filiere || !discipline || !date) return;
    modelValue.value = {
      fonction: filiereDisciplineToId(filiere, discipline),
      date: props.config.date && isBetween(date, props.config.date.min, props.config.date.max) ? date : '',
    };
  }, 200),
  { deep: true },
);

/**
 * Reset la discipline quand la filière change
 */
watch(
  () => form.value.filiere,
  (newValue): void => {
    if (newValue && isReady.value) form.value.discipline = undefined;
  },
);

onMounted((): void => {
  debounce(() => (isReady.value = true), 500)();
  if (!modelValue.value) return;
  const { filiere, discipline } = idToFonction(modelValue.value.fonction);
  form.value = { ...form.value, filiere, discipline };
});
</script>

<template>
  <div class="container">
    <v-autocomplete
      :label="t('person.function.type')"
      v-model="form.filiere"
      v-bind="config.filiere"
      :items="filteredFilieres"
      item-title="libelleFiliere"
      item-value="id"
      :disabled="disableFonctionEdit"
      variant="solo-filled"
      class="w-100"
      hide-details
      required
      flat
    />
    <v-autocomplete
      :label="t('person.function.discipline')"
      v-model="form.discipline"
      v-bind="config.discipline"
      :items="filteredDisciplines"
      item-title="disciplinePoste"
      item-value="id"
      :disabled="!form.filiere || disableFonctionEdit"
      variant="solo-filled"
      class="w-100"
      hide-details
      required
      flat
    />
    <v-text-field
      :label="t('person.function.endDate')"
      type="date"
      v-model="form.date"
      v-bind="config.date"
      variant="solo-filled"
      hide-details
      required
      flat
    />
  </div>
</template>

<style scoped lang="scss">
.container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;

  @media (width >= 700px) {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
