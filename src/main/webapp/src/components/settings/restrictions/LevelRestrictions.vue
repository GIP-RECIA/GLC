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
import type { LevelRestriction } from '@/types/index.ts'
import { faPlus, faTrashCan } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { computed, useId } from 'vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import ClassRestrictions from '@/components/settings/restrictions/ClassRestrictions.vue'

withDefaults(
  defineProps<{
    levelRestriction: LevelRestriction
    isEdit?: boolean
  }>(),
  {
    isEdit: false,
  },
)

const uid = useId()

const id = computed<string>(() => (
  `niveau-${uid}`
))

const modelValue = defineModel<LevelRestriction>({
  default: {
    niveau: '',
    dateRentreeNiveau: null,
    classes: [],
  },
})

function toDisplayDate(
  date: string | undefined | null,
): string | undefined {
  return date
    ? format(date, 'P p')
    : undefined
}
</script>

<template>
  <div class="niveau-card">
    <div class="item">
      <h4
        :class="{
          'sr-only': isEdit,
        }"
      >
        {{ levelRestriction.niveau }}
      </h4>
      <div
        v-if="isEdit"
        class="field"
      >
        <div class="field-layout">
          <div class="field-container">
            <div class="middle">
              <label :for="id">
                {{ levelRestriction.niveau }}
              </label>
              <input
                :id="id"
                v-model="modelValue.dateRentreeNiveau"
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
        :value="toDisplayDate(levelRestriction.dateRentreeNiveau)"
      />
    </div>

    <ul
      v-show="
        isEdit
          ? modelValue.classes.some(c => c.dateRentreeClasse !== null)
          : levelRestriction.classes.some(c => c.dateRentreeClasse !== null)
      "
    >
      <li
        v-for="(classe, index) in levelRestriction.classes"
        v-show="
          isEdit
            ? (
              modelValue.classes[index].dateRentreeClasse !== null
            )
            : (
              classe.dateRentreeClasse !== null
            )
        "
        :key="classe.classe"
      >
        <ClassRestrictions
          v-model="modelValue.classes[index]"
          :class-restriction="classe"
          :is-edit="isEdit"
        />
      </li>
    </ul>

    <div class="grow-1" />

    <footer
      v-if="isEdit"
    >
      <button
        class="btn-secondary small"
        @click="() => {
          modelValue.classes.forEach(c => c.dateRentreeClasse = null)
          modelValue.dateRentreeNiveau = null
        }"
      >
        Supprimer
        <FontAwesomeIcon
          :icon="faTrashCan"
        />
      </button>
      <button
        class="btn-secondary small"
        @click="() => {
          modelValue.classes[0].dateRentreeClasse = ''
        }"
      >
        Ajouter
        <FontAwesomeIcon
          :icon="faPlus"
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

.niveau-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  border-radius: 6px;
  border: 1px solid var(--#{$prefix}stroke);
  padding: 16px;

  > .item {
    > h4 {
      margin-bottom: 4px;
    }
  }

  > ul {
    @include unstyled-list;
    display: grid;
    gap: 16px;
  }

  > footer {
    display: flex;
    justify-content: flex-end;
    gap: 6px;
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > ul {
      grid-template-columns: repeat(auto-fill, minmax(256px, 1fr));
    }
  }
}
</style>
