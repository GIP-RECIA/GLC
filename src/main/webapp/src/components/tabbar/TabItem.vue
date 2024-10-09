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
import type { RouteLocationNamedRaw } from 'vue-router'

defineProps<{
  id: number
  title: string
  link: RouteLocationNamedRaw
  selected: boolean
}>()

defineEmits<(event: 'close', payload: { id: number, selected: boolean }) => void>()
</script>

<template>
  <v-card
    variant="tonal"
    :color="selected ? 'primary' : 'default'"
    class="d-flex align-center mx-1 v-btn v-btn--size-default"
  >
    <router-link class="d-flex align-center text-decoration-none ms-2 h-100" :to="link">
      {{ title }}
    </router-link>
    <v-btn variant="text" density="comfortable" icon @click="$emit('close', { id, selected })">
      <v-icon icon="fas fa-xmark" size="x-small" color="secondary" />
    </v-btn>
  </v-card>
</template>

<style scoped lang="scss">
a {
  color: inherit;
}
</style>
