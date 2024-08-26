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
import InfoCard from '@/components/info/InfoCard.vue';
import { useStructureStore } from '@/stores/index.ts';
import { storeToRefs } from 'pinia';

const structureStore = useStructureStore();
const { personnesByEtat } = storeToRefs(structureStore);
</script>

<template>
  <div class="info-grid">
    <info-card
      v-for="elem in personnesByEtat"
      :key="elem[0]"
      :etat="elem[0]"
      :value="elem[1] ? elem[1].length : 0"
      variant="flat"
    />
  </div>
</template>

<style scoped lang="scss">
.info-grid {
  display: grid;
  gap: 0.75em;
  grid-template-columns: repeat(auto-fill, minmax(15em, 1fr));
}
</style>
