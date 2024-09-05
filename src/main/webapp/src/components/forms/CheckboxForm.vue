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
import type { Filiere } from '@/types';
import { filiereDisciplineToId } from '@/utils';

defineProps<{
  filieres: Array<Filiere> | undefined;
  disabled?: Array<string>;
}>();

const modelValue = defineModel<Array<string>>();
</script>

<template>
  <div class="container">
    <template v-for="filiere in filieres" :key="filiere.codeFiliere">
      <b class="full-width">{{ filiere.libelleFiliere }}</b>
      <v-checkbox
        v-for="discipline in filiere.disciplines"
        :key="filiereDisciplineToId(filiere.id, discipline.id)"
        v-model="modelValue"
        :label="discipline.disciplinePoste"
        :value="filiereDisciplineToId(filiere.id, discipline.id)"
        :disabled="disabled?.includes(filiereDisciplineToId(filiere.id, discipline.id))"
        color="primary"
        :hide-details="true"
      />
    </template>
  </div>
</template>

<style scoped lang="scss">
.container {
  display: grid;
  column-gap: 0.75em;
  grid-template-columns: 1fr;

  @media (width >= 700px) {
    grid-template-columns: 1fr 1fr;

    > .full-width {
      grid-column: span 2;
    }
  }
}
</style>
