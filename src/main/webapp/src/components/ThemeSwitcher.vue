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
import { usePreferredDark } from '@vueuse/core';
import { watch } from 'vue';
import { useTheme } from 'vuetify';

const theme = useTheme();
const isDark = usePreferredDark();

watch(isDark, (newValue) => {
  theme.global.name.value = !newValue ? 'light' : 'dark';
});

const toggleTheme = (): void => {
  theme.global.name.value = theme.global.current.value.dark ? 'light' : 'dark';
};
</script>

<template>
  <v-btn variant="text" density="comfortable" class="ma-0" icon @click="toggleTheme">
    <v-icon :icon="theme.global.name.value == 'light' ? 'fas fa-moon' : 'fas fa-sun'" size="x-small" />
  </v-btn>
</template>
