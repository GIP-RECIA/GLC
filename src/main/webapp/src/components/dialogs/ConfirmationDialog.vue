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
import type { Confirmation } from '@/types'
import { useI18n } from 'vue-i18n'

withDefaults(
  defineProps<{
    title: string
    description?: string
    yesValue: string
    noValue: string
    cancelable?: boolean
    noColor?: string
    yesColor?: string
    maxWidth?: string | number
  }>(),
  {
    cancelable: false,
    maxWidth: 500,
  },
)

const emit = defineEmits({
  close(payload: Confirmation): boolean {
    return !!payload
  },
})

const { t } = useI18n()

const modelValue = defineModel<boolean>()

function onCancel(): void {
  modelValue.value = false
  emit('close', 'cancel')
}

function onNo(): void {
  modelValue.value = false
  emit('close', 'no')
}

function onYes(): void {
  modelValue.value = false
  emit('close', 'yes')
}
</script>

<template>
  <v-dialog v-model="modelValue" persistent :max-width="maxWidth">
    <v-card :title="title" rounded="xl">
      <v-card-text v-if="description">
        {{ description }}
      </v-card-text>
      <v-card-text v-else-if="$slots.description">
        <slot name="description" />
      </v-card-text>

      <v-card-actions>
        <v-btn v-if="cancelable" :text="t('button.cancel')" @click="onCancel" />
        <v-spacer />
        <v-btn :text="t(noValue)" :color="noColor" @click="onNo" />
        <v-btn :text="t(yesValue)" :color="yesColor" @click="onYes" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
