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
import type { enumValues, SimplePersonne } from '@/types'
import { concatenate, getEtat, getIcon } from '@/utils'
import { computed } from 'vue'

const props = defineProps<{
  personne: SimplePersonne
}>()

const etat = computed<enumValues>(() => getEtat(props.personne.etat))
</script>

<template>
  <v-list-item :subtitle="concatenate([personne.email, personne.uid], ' - ')">
    <template #title>
      {{ personne.cn }}
    </template>
    <template #prepend>
      <v-icon :icon="getIcon(personne.source)" :color="etat.color" />
    </template>
  </v-list-item>
</template>
