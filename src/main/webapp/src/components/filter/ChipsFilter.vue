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
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

defineProps<{
  items: Array<{ id: number | string; i18n: string }>;
}>();

const modelValue = defineModel<Array<number | string>>();
</script>

<template>
  <v-chip-group v-model="modelValue" column multiple selected-class="text-primary">
    <v-chip v-for="(tag, index) in items" :key="index" :value="tag.id" :text="t(tag.i18n)" rounded />
  </v-chip-group>
</template>
