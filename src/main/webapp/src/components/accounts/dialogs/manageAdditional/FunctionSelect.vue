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
import type {
  CommonDiscipline,
  FunctionForm,
  PossibleFunction,
} from '@/types/index.ts'
import { debounce } from 'lodash-es'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'

const props = withDefaults(
  defineProps<{
    possible?: PossibleFunction[]
    disableFonctionEdit?: boolean
    config?: {
      filiere?: Record<string, any>
      discipline?: Record<string, any>
      startDate?: Record<string, any>
      endDate?: Record<string, any>
    }
  }>(),
  {
    disableFonctionEdit: false,
    config: () => ({
      filiere: {},
      discipline: {},
      startDate: {},
      endDate: {},
    }),
  },
)

const modelValue = defineModel<FunctionForm>({
  default: {
    filiere: undefined,
    discipline: undefined,
    dateDebut: undefined,
    dateFin: undefined,
  },
})

const { t } = useI18n()

const filteredDisciplines = computed<CommonDiscipline[] | undefined>(() => {
  return props.possible
    ?.find(({ id }) => id === modelValue.value.filiere)
    ?.disciplines
})

/* reset discipline when filiere change */

const isReady = ref<boolean>(false)

watch(
  () => props.possible,
  (val) => {
    if (val)
      debounce(() => (isReady.value = true), 500)()
  },
)

watch(
  () => modelValue.value.filiere,
  (newValue): void => {
    if (newValue && isReady.value)
      modelValue.value.discipline = undefined
  },
)
</script>

<template>
  <div class="function-select">
    <v-autocomplete
      v-model="modelValue.filiere"
      :label="t('person.function.type')"
      :items="possible"
      item-title="libelle"
      item-value="id"
      :disabled="disableFonctionEdit"
      variant="solo-filled"
      class="w-100"
      hide-details
      required
      flat
      v-bind="config.filiere"
    />

    <v-autocomplete
      v-model="modelValue.discipline"
      :label="t('person.function.discipline')"
      :items="filteredDisciplines"
      item-title="libelle"
      item-value="id"
      :disabled="!modelValue.filiere || disableFonctionEdit"
      variant="solo-filled"
      class="w-100"
      hide-details
      required
      flat
      v-bind="config.discipline"
    />

    <div class="field">
      <div class="field-layout">
        <div class="field-container">
          <div class="middle">
            <label for="startDate">
              {{ t('person.function.startDate') }}
            </label>
            <input
              id="startDate"
              v-model="modelValue.dateDebut"
              type="date"
              placeholder=""
              v-bind="config.startDate"
            >
          </div>
        </div>
        <div class="active-indicator" />
      </div>
    </div>

    <div class="field">
      <div class="field-layout">
        <div class="field-container">
          <div class="middle">
            <label for="endDate">
              {{ t('person.function.endDate') }}
            </label>
            <input
              id="endDate"
              v-model="modelValue.dateFin"
              type="date"
              placeholder=""
              v-bind="config.endDate"
            >
          </div>
        </div>
        <div class="active-indicator" />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.function-select {
  display: grid;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
