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
import type { Filiere } from '@/types/filiereType.ts';
import { computed } from 'vue';

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  selected?: Array<string>;
  disabled?: Array<string>;
}>();

const emit = defineEmits<(event: 'update:selected', payload: Array<string>) => void>();

const checked = computed<Array<string>>({
  get() {
    return props.selected ? props.selected : [];
  },
  set(newValue) {
    emit('update:selected', newValue);
  },
});
</script>

<template>
  <div v-for="(filiere, index) in filieres" :key="index">
    <div>
      <b>{{ filiere.libelleFiliere }}</b>
    </div>
    <div class="d-flex flex-row flex-wrap">
      <div v-for="(discipline, index) in filiere.disciplines" :key="index" class="modal-flex-item">
        <v-checkbox
          v-model="checked"
          :label="discipline.disciplinePoste"
          :value="`${filiere.id}-${discipline.id}`"
          :disabled="disabled?.includes(`${filiere.id}-${discipline.id}`)"
          color="primary"
          :hide-details="true"
        />
      </div>
    </div>
  </div>
</template>
