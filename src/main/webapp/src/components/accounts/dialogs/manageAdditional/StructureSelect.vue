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
import type { SearchStructure } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import { concatenate } from '@/utils/index.ts'

defineProps<{
  structures?: SearchStructure[]
}>()

const modelValue = defineModel<SearchStructure | undefined>()

// eslint-disable-next-line unused-imports/no-unused-vars
const { t } = useI18n()
</script>

<template>
  <v-autocomplete
    v-model="modelValue"
    :items="structures"
    item-value="id"
    item-title="nom"
    :filter-keys="['raw.nom', 'raw.type', 'raw.uai', 'raw.siren']"
    variant="solo-filled"
    hide-details
    return-object
    flat
  >
    <template #item="{ props: itemProps, item }">
      <v-list-item
        v-bind="itemProps"
        :subtitle="
          concatenate(
            [item.raw.type, item.raw.uai],
            ' ',
          )
        "
      >
        <template #title>
          {{ item.raw.nom }}
        </template>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
