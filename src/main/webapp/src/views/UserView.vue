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
import { storeToRefs } from 'pinia'
import { watch } from 'vue'
import { useRoute } from 'vue-router'
import PersonneDialogInfo from '@/components/dialogs/personne/PersonneDialogInfo.vue'
import PersonneDialogManageAdditional from '@/components/dialogs/personne/PersonneDialogManageAdditional.vue'
import { usePersonne } from '@/composables/index.ts'
import { usePersonneStore } from '@/stores/index.ts'
import { PersonneDialogState } from '@/types/enums/index.ts'

const personneStore = usePersonneStore()
const { initCurrentPersonne } = personneStore
const {
  currentPersonne,
  dialogState,
  dialogTitle,
} = storeToRefs(personneStore)

const { canEditAdditionals } = usePersonne()

const route = useRoute()

watch(
  () => route.params.userId,
  (userId) => {
    if (userId && typeof userId === 'string')
      initCurrentPersonne(Number(userId))
  },
  {
    immediate: true,
  },
)
</script>

<template>
  <v-container>
    <h1>{{ dialogTitle }}</h1>

    <PersonneDialogInfo
      v-if="dialogState === PersonneDialogState.Info"
      :personne="currentPersonne"
    />

    <PersonneDialogManageAdditional
      v-if="(dialogState === PersonneDialogState.ManageAdditional
        || dialogState === PersonneDialogState.ManageAdditionalMultiple)
        && canEditAdditionals"
    />
  </v-container>
</template>

<style scoped lang="scss">
</style>
