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
import type { Discipline, Filiere, FonctionForm } from '@/types/index.ts'
import { debounce } from 'lodash-es'
import { computed, onBeforeMount, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { filiereDisciplineToId, isBetween } from '@/utils/index.ts'

const props = withDefaults(
  defineProps<{
    filieres?: Filiere[]
    disabled?: string[]
    disableFonctionEdit?: boolean
    config?: {
      filiere?: Record<string, any>
      discipline?: Record<string, any>
      date?: Record<string, any>
    }
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
)

const { t } = useI18n()

const modelValue = defineModel<FonctionForm>()

const form = ref<FonctionForm>({
  filiere: undefined,
  discipline: undefined,
  dateFin: undefined,
})

const isReady = ref<boolean>(false)

const filteredFilieres = computed<Filiere[] | undefined>(() => {
  if (!props.disabled || props.disableFonctionEdit)
    return props.filieres
  return props.filieres
    ?.map((filiere) => {
      const disciplines: Discipline[] = filiere.disciplines.filter(
        discipline => !props.disabled?.includes(
          filiereDisciplineToId(filiere.id, discipline.id),
        ),
      )

      return { ...filiere, disciplines }
    })
    .filter(({ disciplines }) => disciplines.length > 0)
})

const filteredDisciplines = computed<Discipline[] | undefined>(() => {
  return filteredFilieres.value
    ?.find(({ id }) => id === form.value.filiere)
    ?.disciplines
})

/**
 * Update modelValue quand toutes les données sont replies
 */
watch(
  form,
  debounce((newValue: FonctionForm): void => {
    if (!isReady.value)
      return
    const { filiere, discipline, dateFin } = newValue

    const isDateOk = props.config.date?.min && props.config.date?.max
      ? isBetween(dateFin ?? '', props.config.date.min, props.config.date.max)
      : dateFin !== ''
    modelValue.value = {
      filiere,
      discipline,
      dateFin: isDateOk
        ? dateFin
        : undefined,
    }
  }, 200),
  { deep: true },
)

/**
 * Reset la discipline quand la filière change
 */
watch(
  () => form.value.filiere,
  (newValue): void => {
    if (newValue && isReady.value)
      form.value.discipline = undefined
  },
)

onBeforeMount((): void => {
  debounce(() => (isReady.value = true), 500)()
  if (!modelValue.value?.filiere || !modelValue.value?.discipline)
    return
  form.value = {
    ...form.value,
    ...modelValue.value,
  }
})
</script>

<template>
  <div class="fonction-form-container">
    <v-autocomplete
      v-model="form.filiere"
      :label="t('person.function.type')"
      v-bind="config.filiere"
      :items="filteredFilieres"
      item-title="libelleFiliere"
      item-value="id"
      :disabled="disableFonctionEdit"
      variant="solo-filled"
      class="w-100"
      hide-details
      required
    />
    <v-autocomplete
      v-model="form.discipline"
      :label="t('person.function.discipline')"
      v-bind="config.discipline"
      :items="filteredDisciplines"
      item-title="disciplinePoste"
      item-value="id"
      :disabled="!form.filiere || disableFonctionEdit"
      variant="solo-filled"
      class="w-100"
      hide-details
      required
    />
    <v-text-field
      v-model="form.dateFin"
      :label="t('person.function.endDate')"
      type="date"
      v-bind="config.date"
      variant="solo-filled"
      hide-details
      required
    />
  </div>
</template>

<style scoped lang="scss">
.fonction-form-container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;

  @media (width >= 700px) {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
