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
import type { SimplePersonne } from '@/types'
import { useStructureStore } from '@/stores'
import { Etat } from '@/types/enums'
import { storeToRefs } from 'pinia'
import { computed } from 'vue'
import InfoCard from './InfoCard.vue'

const isDev = import.meta.env.DEV

const structureStore = useStructureStore()
const { personnesByEtat } = storeToRefs(structureStore)

const items = computed<Map<Etat, Array<SimplePersonne> | undefined>>(() => {
  if (isDev)
    return personnesByEtat.value
  return new Map(
    [...personnesByEtat.value]?.filter(([key]) =>
      [Etat.Invalide, Etat.Valide, Etat.Bloque, Etat.Delete, Etat.Deleting, Etat.Incertain].includes(key),
    ),
  )
})
</script>

<template>
  <div class="info-grid">
    <InfoCard v-for="elem in items" :key="elem[0]" :etat="elem[0]" :value="elem[1] ? elem[1].length : 0" />
  </div>
</template>

<style scoped lang="scss">
.info-grid {
  display: grid;
  gap: 0.75em;
  grid-template-columns: repeat(auto-fill, minmax(15em, 1fr));
}
</style>
