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
import { useI18n } from 'vue-i18n'

defineProps<{
  items: Array<{ i18n: string, value: any, color?: string }>
}>()

const { t } = useI18n()

const modelValue = defineModel<Array<any>>()
</script>

<template>
  <v-select
    v-model="modelValue"
    :items="items"
    rounded="xl"
    variant="solo"
    density="compact"
    prepend-inner-icon="fas fa-filter"
    flat
    chips
    multiple
    hide-details
    class="select-filter"
  >
    <template #chip="{ props, item }">
      <v-chip v-bind="props" rounded>
        <div class="d-flex flex-row align-center">
          <v-icon v-if="item.raw.color" icon="fas fa-circle" :color="item.raw.color" size="md" class="me-2" />
          <div>{{ t(item.raw.i18n) }}</div>
        </div>
      </v-chip>
    </template>
    <template #item="{ props, item }">
      <v-list-item v-bind="props" title="">
        <div class="d-flex flex-row align-center">
          <v-icon v-if="item.raw.color" icon="fas fa-circle" :color="item.raw.color" size="md" class="me-2" />
          <div>{{ t(item.raw.i18n) }}</div>
        </div>
      </v-list-item>
    </template>
  </v-select>
</template>

<style scoped lang="scss">
.select-filter {
  max-width: fit-content;
}
</style>
