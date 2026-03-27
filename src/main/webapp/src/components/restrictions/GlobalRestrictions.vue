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
import { faFloppyDisk, faPen, faXmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { saveRestrictions } from '@/services/api'

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
  // eslint-disable-next-line ts/no-use-before-define
  enabeled.value !== props.restrictions.enabled
))

const enabeled = ref<boolean>(false)

watch(
  () => props.restrictions,
  (val) => {
    if (!val)
      return

    enabeled.value = props.restrictions.enabled
  },
)

function toggleEdit(): void {
  if (isEdit.value)
    enabeled.value = props.restrictions.enabled
  isEdit.value = !isEdit.value
  emit('edit', isEdit.value)
}

function save(): void {
  if (!props.etabId)
    return

  const body = {
    ...props.restrictions,
    enabled: enabeled.value,
  }

  saveRestrictions({
    id: props.etabId,
    body,
  })
  toggleEdit()
  emit('update', body)
}
</script>

<template>
  <div class="r-card gloabl-restrictions-card">
    <div class="body">
      <div class="item">
        <h3>
          Etat
        </h3>
        <div
          v-if="isEdit"
        >
          <input
            id="enabled"
            v-model="enabeled"
            type="checkbox"
          >
          <label for="enabled">{{ enabeled ? 'Activé' : 'Desactivé' }}</label>
        </div>
        <SafeEmptyData
          v-else
          :value="restrictions ? restrictions.enabled ? 'Activé' : 'Desactivé' : undefined"
        />
      </div>
    </div>

    <footer
      v-if="restrictions && canEdit"
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

.gloabl-restrictions-card {
  > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .item {
      > h3 {
        margin-bottom: 4px;
      }
    }
  }
}
</style>
