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
import type { LevelRestriction, StructureRestriction } from '@/types/index.ts'
import { faFloppyDisk, faPen, faPlus, faXmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { cloneDeep } from 'lodash-es'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import MenuButton from '@/components/MenuButton.vue'
import LevelRestrictions from '@/components/restrictions/LevelRestrictions.vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { saveRestrictions } from '@/services/api/index.ts'
import { formatDateTime, toDateTime, toISOString } from '@/utils/index.ts'

const props = withDefaults(
  defineProps<{
    etabId?: number
    restrictions?: StructureRestriction
    canEdit?: boolean
    disableEdit?: boolean
  }>(),
  {
    canEdit: true,
    disableEdit: false,
  },
)

const emit = defineEmits<{
  edit: [boolean]
  update: [restrictions: StructureRestriction]
}>()

const { t } = useI18n()

const isEdit = ref<boolean>(false)

const canSave = computed<boolean>(() => (
  true
))

const EMPTY_RESTRICTION: StructureRestriction = {
  enabled: true,
  dateRentreeEtab: null,
  niveaux: [],
}

const fields = ref<StructureRestriction>(EMPTY_RESTRICTION)
let initalFields: StructureRestriction = EMPTY_RESTRICTION

watch(
  () => props.restrictions,
  (val) => {
    if (!val)
      return

    const niveaux = val.niveaux.map((level) => {
      const classes = level.classes.map((classe) => {
        return {
          ...classe,
          dateRentreeClasse: toDateTime(classe.dateRentreeClasse),
        }
      })

      return {
        ...level,
        dateRentreeNiveau: toDateTime(level.dateRentreeNiveau),
        classes,
      }
    })

    initalFields = {
      enabled: val.enabled,
      dateRentreeEtab: toDateTime(val.dateRentreeEtab),
      niveaux,
    }
    fields.value = cloneDeep(initalFields)
  },
  {
    immediate: true,
    deep: true,
  },
)

function hasLevel(level: LevelRestriction, index: number) {
  const l = isEdit.value
    ? fields.value.niveaux[index]
    : level

  return (
    l.dateRentreeNiveau !== null
    || l.classes.some(c => c.dateRentreeClasse !== null)
  )
}

function toggleEdit(): void {
  if (isEdit.value)
    fields.value = cloneDeep(initalFields)
  isEdit.value = !isEdit.value
  emit('edit', isEdit.value)
}

function save(): void {
  if (!props.etabId)
    return

  const body = {
    enabled: fields.value.enabled,
    dateRentreeEtab: toISOString(fields.value.dateRentreeEtab),
    niveaux: fields.value.niveaux.map((level) => {
      const classes = level.classes.map((classe) => {
        return {
          ...classe,
          dateRentreeClasse: toISOString(classe.dateRentreeClasse),
        }
      })

      return {
        ...level,
        dateRentreeNiveau: toISOString(level.dateRentreeNiveau),
        classes,
      }
    }),
  }

  saveRestrictions({
    id: props.etabId,
    body,
  })
  toggleEdit()
  emit('update', body)
}

const addableLevels = computed<{ uid: string, name: string }[]>(() => (
  fields.value?.niveaux
    .filter(level => (
      level.dateRentreeNiveau === null
      && level.classes.every(c => c.dateRentreeClasse === null)
    ))
    .map(level => ({ uid: level.niveau, name: level.niveau }))
    ?? []
))

function addLevel(uid: string | number): void {
  const level = fields.value.niveaux.find(level => level.niveau === uid)
  if (level)
    level.dateRentreeNiveau = ''
}
</script>

<template>
  <div class="r-card open-restrictions-card">
    <div class="body">
      <div class="item">
        <h3
          :class="{
            'sr-only': isEdit,
          }"
        >
          Etablissement
        </h3>
        <div
          v-if="isEdit"
          class="field"
        >
          <div class="field-layout">
            <div class="field-container">
              <div class="middle">
                <label for="dateRentreeEtab">
                  Etablissement
                </label>
                <input
                  id="dateRentreeEtab"
                  v-model="fields.dateRentreeEtab"
                  type="datetime-local"
                  placeholder=""
                >
              </div>
            </div>
            <div class="active-indicator" />
          </div>
        </div>
        <SafeEmptyData
          v-else
          :value="formatDateTime(restrictions?.dateRentreeEtab)"
        />
      </div>

      <div v-if="restrictions">
        <h3>
          Niveaux
        </h3>
        <div class="niveau-container">
          <LevelRestrictions
            v-for="(niveau, index) in restrictions.niveaux"
            v-show="hasLevel(niveau, index)"
            :key="`restriction-level-${niveau.niveau}`"
            v-model="fields.niveaux[index]"
            :level-restriction="niveau"
            :is-edit="isEdit"
          />
        </div>
      </div>
    </div>

    <footer
      v-if="restrictions && canEdit"
    >
      <MenuButton
        v-show="isEdit"
        :items="addableLevels"
        :disabled="addableLevels.length === 0"
        btn-class="btn-secondary small"
        @update:model-value="addLevel"
      >
        Ajouter
        <FontAwesomeIcon
          :icon="faPlus"
        />
      </MenuButton>
      <button
        :disabled="disableEdit && !isEdit"
        class="small"
        :class="[isEdit ? 'btn-secondary' : 'btn-primary']"
        @click="toggleEdit"
      >
        {{ t(`button.${isEdit ? 'cancel' : 'edit'}`) }}
        <FontAwesomeIcon
          :icon="isEdit ? faXmark : faPen"
        />
      </button>
      <button
        v-show="isEdit"
        :disabled="!canSave"
        class="btn-primary small"
        @click="save"
      >
        {{ t('button.save') }}
        <FontAwesomeIcon
          :icon="faFloppyDisk"
        />
      </button>
    </footer>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.open-restrictions-card {
  > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .item {
      > h3 {
        margin-bottom: 4px;
      }
    }

    > div > .niveau-container {
      display: grid;
      gap: 16px;
      align-items: start;
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > .body {
      > div > .niveau-container {
        grid-template-columns: repeat(auto-fill, minmax(512px, 1fr));
      }
    }
  }
}
</style>
