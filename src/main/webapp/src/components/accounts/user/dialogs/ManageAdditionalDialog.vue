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
  User,
  UserStructure,
} from '@/types/index.ts'
import {
  faFloppyDisk,
  faTrashCan,
  faXmark,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { debounce } from 'lodash-es'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  usePossibleFunctionsQuery,
  useRemoveUserOneAdditionalMutation,
  useSetUserOneAdditionalMutation,
} from '@/services/queries/index.ts'

const props = defineProps<{
  user?: User
  structure?: UserStructure
  fonction?: FunctionForm
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'save': []
}>()

const modelValue = defineModel<boolean>({ required: true })

const { t } = useI18n()

const structureId = computed<number>(() => (
  props.structure?.id ?? Number.NaN
))

const disableFonctionEdit = computed<boolean>(() => (
  !!props.fonction
))

const canSave = computed<boolean>(() => (
  true
))

const EMPTY_FUNCTION: FunctionForm = {
  filiere: undefined,
  discipline: undefined,
  dateDebut: undefined,
  dateFin: undefined,
}

const fields = ref<FunctionForm>(EMPTY_FUNCTION)

watch(
  () => props.fonction,
  (val) => {
    if (!val) {
      fields.value = EMPTY_FUNCTION

      return
    }

    fields.value = val
  },
)

const disabled = computed<FunctionForm[]>(() => {
  const data: FunctionForm[] = []
  if (!props.structure || disableFonctionEdit.value) {
    return data
  }

  [
    ...props.structure.fonctions,
    ...props.structure.additionalFonctions,
  ].forEach(({ id, disciplines }) => {
    data.push(...disciplines.map(discipline => ({
      filiere: id,
      discipline: discipline.id,
      dateDebut: discipline.dateDebut,
      dateFin: discipline.dateFin,
    })))
  })

  return data
})

const { data } = usePossibleFunctionsQuery(structureId)

const filteredFilieres = computed<PossibleFunction[] | undefined>(() => {
  if (!disabled.value)
    return data.value
  return data.value
    ?.map((filiere) => {
      const disciplines: CommonDiscipline[] = filiere.disciplines.filter(
        discipline => !disabled.value
          .map(({ filiere: fi, discipline: di }) => (
            `${fi}-${di}`
          ))
          .includes(
            `${filiere.id}-${discipline.id}`,
          ),
      )

      return { ...filiere, disciplines }
    })
    .filter(({ disciplines }) => disciplines.length > 0)
})

const filteredDisciplines = computed<CommonDiscipline[] | undefined>(() => {
  return filteredFilieres.value
    ?.find(({ id }) => id === fields.value.filiere)
    ?.disciplines
})

const isReady = ref<boolean>(false)

watch(
  data,
  (val) => {
    if (val)
      debounce(() => (isReady.value = true), 500)()
  },
)

/*
 * reset discipline when filiere change
 */
watch(
  () => fields.value.filiere,
  (newValue): void => {
    if (newValue && isReady.value)
      fields.value.discipline = undefined
  },
)

/* Actions */

async function remove(): Promise<void> {
  const {
    user,
    structure,
  } = props
  if (!user || !structure)
    return

  const { mutate } = useRemoveUserOneAdditionalMutation()
  mutate({
    id: user.id,
    structureId: structure.id,
    toDeleteFunction: fields.value,
    requiredAction: 'save',
  })
  emit('update:modelValue', false)
}

function close(): void {
  emit('update:modelValue', false)
}

async function save(): Promise<void> {
  const {
    user,
    structure,
  } = props
  if (!user || !structure)
    return

  const { mutate } = useSetUserOneAdditionalMutation()
  mutate({
    id: user.id,
    structureId: structure.id,
    toAddFunction: fields.value,
    requiredAction: 'save',
  })
  emit('update:modelValue', false)
}
</script>

<template>
  <v-dialog
    v-model="modelValue"
    scrollable
    :max-width="1024"
  >
    <v-card rounded="xl">
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title>
          <h1>
            {{ }}
          </h1>
        </v-toolbar-title>
        <template #append>
          <v-btn
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            class="me-1"
            @click="close"
          />
        </template>
      </v-toolbar>

      <v-card-text class="pt-0 py-3 manage-additional-dialog-container">
        <v-autocomplete
          v-model="fields.filiere"
          :label="t('person.function.type')"
          :items="filteredFilieres"
          item-title="libelle"
          item-value="id"
          :disabled="disableFonctionEdit"
          variant="solo-filled"
          class="w-100"
          hide-details
          required
          flat
        />
        <v-autocomplete
          v-model="fields.discipline"
          :label="t('person.function.discipline')"
          :items="filteredDisciplines"
          item-title="libelle"
          item-value="id"
          :disabled="!fields.filiere || disableFonctionEdit"
          variant="solo-filled"
          class="w-100"
          hide-details
          required
          flat
        />
        <v-text-field
          v-model="fields.dateDebut"
          :label="t('person.function.startDate')"
          type="date"
          variant="solo-filled"
          hide-details
          required
          flat
        />
        <v-text-field
          v-model="fields.dateFin"
          :label="t('person.function.endDate')"
          type="date"
          variant="solo-filled"
          hide-details
          required
          flat
        />
      </v-card-text>

      <v-card-actions>
        <button
          type="button"
          :disabled="!fonction"
          class="btn-secondary"
          @click="remove"
        >
          {{ t('button.delete') }}
          <FontAwesomeIcon
            :icon="faTrashCan"
          />
        </button>
        <div class="grow-1" />
        <button
          type="button"
          class="btn-secondary"
          @click="close"
        >
          {{ t('button.cancel') }}
          <FontAwesomeIcon
            :icon="faXmark"
          />
        </button>
        <button
          type="button"
          :disabled="!canSave"
          class="btn-primary"
          @click="save"
        >
          {{ t('button.save') }}
          <FontAwesomeIcon
            :icon="faFloppyDisk"
          />
        </button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
