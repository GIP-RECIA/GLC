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
import type { Etablissement } from '@/types/index.ts'
import { faFloppyDisk, faPen, faXmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'

const props = withDefaults(
  defineProps<{
    etab?: Etablissement
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

const siteWeb = ref<string | undefined>(props.etab?.siteWeb)

const canSave = computed<boolean>(() => (
  isEdit.value
  && siteWeb.value !== props.etab?.siteWeb
))

function toggleEdit(): void {
  isEdit.value = !isEdit.value
  emit('edit', isEdit.value)
}

function save(): void {
  isEdit.value = false
  emit('edit', false)
}

watch(
  () => props.etab?.siteWeb,
  (newValue) => {
    siteWeb.value = newValue
  },
)
</script>

<template>
  <div class="r-card info-card">
    <header>
      <h3>Contact</h3>
    </header>

    <div class="body">
      <ul>
        <li class="full-width">
          <h4>Mail</h4>
          <SafeEmptyData
            :value="etab?.mail"
          />
        </li>
        <li class="full-width">
          <h4
            :class="{
              'sr-only': isEdit,
            }"
          >
            Site web
          </h4>
          <div
            v-if="isEdit"
            class="field"
          >
            <div class="field-layout">
              <div class="field-container">
                <div class="middle">
                  <label
                    for="siteWeb"
                  >
                    Site web
                  </label>
                  <input
                    id="siteWeb"
                    v-model.trim="siteWeb"
                    type="text"
                    placeholder=""
                  >
                </div>
              </div>
              <div class="active-indicator" />
            </div>
          </div>
          <SafeEmptyData
            v-else
            :value="etab?.siteWeb"
          />
        </li>
      </ul>
    </div>

    <footer
      v-if="etab && canEdit"
    >
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

.info-card {
  > .body {
    > ul {
      @include unstyled-list;
      display: flex;
      flex-direction: column;
      gap: 16px;

      > li {
        > h4 {
          margin-bottom: 4px;
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, xl)) {
    > .body > ul {
      display: grid;
      grid-template-columns: repeat(2, 1fr);

      > .full-width {
        grid-column: span 2;
      }
    }
  }
}
</style>
