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
  SearchStructure,
  SearchUser,
  UserFunction,
} from '@/types/index.ts'
import {
  faFloppyDisk,
  faTrashCan,
  faXmark,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { debounce } from 'lodash-es'
import { computed, ref, toRefs, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { searchUser } from '@/services/api/index.ts'
import {
  usePossibleFunctionsQuery,
  useRemoveUserOneAdditionalMutation,
  useSetUserOneAdditionalMutation,
  useStructuresQuery,
} from '@/services/queries/index.ts'
import { toISODate } from '@/utils/index.ts'
import FunctionSelect from './manageAdditional/FunctionSelect.vue'
import StructureSelect from './manageAdditional/StructureSelect.vue'
import UserSelect from './manageAdditional/UserSelect.vue'

const props = withDefaults(
  defineProps<{
    title: string
    userId?: number
    structureId?: number
    disabledFonctions?: UserFunction[]
    editFonction?: FunctionForm
  }>(),
  {
    structureId: Number.NaN,
  },
)

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'save': []
}>()

const modelValue = defineModel<boolean>({ required: true })

const { t } = useI18n()

const { structureId: structureIdRef } = toRefs(props)

const disableFonctionEdit = computed<boolean>(() => (
  !!props.editFonction
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
  () => props.editFonction,
  (val) => {
    if (!val) {
      fields.value = EMPTY_FUNCTION

      return
    }

    fields.value = {
      ...val,
      dateDebut: toISODate(val.dateDebut),
      dateFin: toISODate(val.dateFin),
    }
  },
)

const disabled = computed<FunctionForm[]>(() => {
  const data: FunctionForm[] = []
  if (disableFonctionEdit.value)
    return data

  props.disabledFonctions?.forEach(({ id, disciplines }) => {
    data.push(...disciplines.map(discipline => ({
      filiere: id,
      discipline: discipline.id,
      dateDebut: discipline.dateDebut,
      dateFin: discipline.dateFin,
    })))
  })

  return data
})

const { data } = usePossibleFunctionsQuery(structureIdRef)

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

const selectedUser = ref<SearchUser>()

const searchUserValue = ref<string>()

const searchUsers = ref<SearchUser[]>()

watch(
  searchUserValue,
  (val) => {
    if (!val)
      return

    debounce(() => getSearchUsers(val), 500)()
  },
)

async function getSearchUsers(q: string): Promise<void> {
  const response = await searchUser(q, {
    staff: true,
    check_rights: false,
  })

  searchUsers.value = response
}

const selectedStructure = ref<SearchStructure>()

const { data: searchStructures } = useStructuresQuery()

/* Actions */

async function remove(): Promise<void> {
  const {
    userId,
    structureId,
  } = props
  if (!userId || !structureId)
    return

  const { mutate } = useRemoveUserOneAdditionalMutation()
  mutate({
    id: userId,
    structureId,
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
    userId,
    structureId,
  } = props
  if (!userId || !structureId)
    return

  const { mutate } = useSetUserOneAdditionalMutation()
  mutate({
    id: userId,
    structureId,
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
            {{ title }}
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

      <v-card-text class="pt-0 py-3">
        <UserSelect
          v-if="!userId"
          v-model="selectedUser"
          v-model:search="searchUserValue"
          :users="searchUsers"
        />

        <StructureSelect
          v-if="!structureId"
          v-model="selectedStructure"
          :structures="searchStructures"
        />

        <FunctionSelect
          v-model="fields"
          :possible="filteredFilieres"
          :disable-fonction-edit="!!editFonction"
        />
      </v-card-text>

      <v-card-actions>
        <button
          v-show="userId && structureId"
          type="button"
          :disabled="!editFonction"
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

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

h1 {
  margin-bottom: 0;
}
</style>
