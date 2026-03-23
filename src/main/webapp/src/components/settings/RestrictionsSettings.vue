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
import type { StructureRestriction } from '@/types/index.ts'
import { faFloppyDisk, faPen, faPlus, faRotateLeft, faXmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format, formatISO } from 'date-fns'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import MenuButton from '@/components/MenuButton.vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import LevelRestrictions from '@/components/settings/restrictions/LevelRestrictions.vue'

const props = withDefaults(
  defineProps<{
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
}>()

const { t } = useI18n()

const isEdit = ref<boolean>(false)

const canSave = computed<boolean>(() => (
  true
))

function toggleEdit(): void {
  isEdit.value = !isEdit.value
  emit('edit', isEdit.value)
}

function save(): void {
  isEdit.value = false
  emit('edit', false)
}

function toDisplayDate(
  date: string | undefined | null,
): string | undefined {
  return date
    ? format(date, 'P p')
    : undefined
}

function toDateTime(date: string | null): string | null {
  return date !== null
    ? `${formatISO(date, { representation: 'date' })}T${format(date, 'HH:mm')}`
    : null
}

const EMPTY_RESTRICTION: StructureRestriction = {
  dateRentreeEtab: null,
  niveaux: [],
}

const fields = ref<StructureRestriction>(EMPTY_RESTRICTION)

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

    fields.value = {
      dateRentreeEtab: toDateTime(val.dateRentreeEtab),
      niveaux,
    }
  },
  {
    immediate: true,
    deep: true,
  },
)

const addableLevels = computed<{ uid: string, name: string }[]>(() => (
  fields.value?.niveaux
    .filter(level => level.dateRentreeNiveau === null)
    .map(level => ({ uid: level.niveau, name: level.niveau }))
    ?? []
))

function addLevel(uid: string | number): void {
  const level = fields.value.niveaux.find(level => level.niveau === uid)
  if (level)
    level.dateRentreeNiveau = ''
}

function clearEtab(): void {
  fields.value.dateRentreeEtab = ''
}
</script>

<template>
  <div class="r-card restriction-card">
    <header>
      <h3>Date de rentrée</h3>
    </header>

    <div class="body">
      <div class="item">
        <h4
          :class="{
            'sr-only': isEdit,
          }"
        >
          Etablissement
        </h4>
        <div
          v-if="isEdit"
          class="field"
        >
          <div class="field-layout">
            <div class="field-container">
              <div class="middle">
                <label
                  for="dateRentreeEtab"
                >
                  Etablissement
                </label>
                <input
                  id="dateRentreeEtab"
                  v-model="fields.dateRentreeEtab"
                  type="datetime-local"
                  placeholder=""
                >
              </div>
              <div class="end">
                <button
                  title="Réinitialiser"
                  class="btn-tertiary circle"
                  @click="clearEtab"
                >
                  <FontAwesomeIcon
                    :icon="faRotateLeft"
                  />
                </button>
              </div>
            </div>
            <div class="active-indicator" />
          </div>
        </div>
        <SafeEmptyData
          v-else
          :value="toDisplayDate(restrictions?.dateRentreeEtab)"
        />
      </div>

      <div
        v-if="restrictions"
        class="niveau-container"
      >
        <LevelRestrictions
          v-for="(niveau, index) in restrictions.niveaux"
          v-show="
            isEdit
              ? (
                fields.niveaux[index].dateRentreeNiveau !== null
                || fields.niveaux[index].classes.some(c => c.dateRentreeClasse !== null)
              )
              : (
                niveau.dateRentreeNiveau !== null
                || niveau.classes.some(c => c.dateRentreeClasse !== null)
              )
          "
          :key="niveau.niveau"
          v-model="fields.niveaux[index]"
          :level-restriction="niveau"
          :is-edit="isEdit"
        />
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

.restriction-card {
  display: flex;
  flex-direction: column;

  > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .item {
      > h4 {
        margin-bottom: 4px;
      }
    }

    > .niveau-container {
      display: grid;
      gap: 16px;
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > .body {
      > .niveau-container {
        grid-template-columns: repeat(auto-fill, minmax(512px, 1fr));
      }
    }
  }
}
</style>
