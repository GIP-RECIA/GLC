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
import type { ClassRestriction } from '@/types/index.ts'
import { faRotateLeft, faTrashCan } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { computed, useId } from 'vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'

withDefaults(
  defineProps<{
    classRestriction: ClassRestriction
    isEdit?: boolean
  }>(),
  {
    isEdit: false,
  },
)

const uid = useId()

const id = computed<string>(() => (
  `niveau-classe-${uid}`
))

const modelValue = defineModel<ClassRestriction>({
  default: {
    classe: '',
    dateRentreeClasse: null,
  },
})

function toDisplayDate(
  date: string | undefined | null,
): string | undefined {
  return date
    ? format(date, 'P p')
    : undefined
}

function deleteClass(): void {
  modelValue.value.dateRentreeClasse = null
}

function clearClass(): void {
  modelValue.value.dateRentreeClasse = ''
}
</script>

<template>
  <div class="item">
    <h5
      class="h4"
      :class="{
        'sr-only': isEdit,
      }"
    >
      {{ classRestriction.classe }}
    </h5>
    <div
      v-if="isEdit"
      class="field-layout"
    >
      <div class="field">
        <div class="field-layout">
          <div class="field-container">
            <div class="middle">
              <label :for="id">
                {{ classRestriction.classe }}
              </label>
              <input
                :id="id"
                v-model="modelValue.dateRentreeClasse"
                type="datetime-local"
                placeholder=""
              >
            </div>
            <div class="end">
              <button
                title="Réinitialiser"
                class="btn-tertiary circle"
                @click="clearClass"
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
      <button
        class="btn-tertiary circle"
        @click="deleteClass"
      >
        <FontAwesomeIcon
          :icon="faTrashCan"
        />
      </button>
    </div>
    <SafeEmptyData
      v-else
      :value="toDisplayDate(classRestriction.dateRentreeClasse)"
    />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.item {
  > h5 {
    margin-bottom: 4px;
  }

  > .field-layout {
    display: flex;
    align-items: center;
    gap: 8px;

    > .field {
      flex: 1 1 auto;
    }
  }
}
</style>
