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
import type { RightMember, ServiceRight, ServiceRights } from '@/types/index.ts'
import { storeToRefs } from 'pinia'
import { ref, watchEffect } from 'vue'
import ServicesRightsLayout from '@/components/layouts/ServicesRightsLayout.vue'
import { getRights } from '@/services/api/index.ts'
import { useStructureStore } from '@/stores'

const structureStore = useStructureStore()
structureStore.init()
const { etabs } = storeToRefs(structureStore)

const currentEtab = ref<number>()

const data = ref<ServiceRights[]>()
const dataState = ref<'UNLOADED' | 'LOADING' | 'LOADED' | 'ERROR'>('UNLOADED')

watchEffect(async (): Promise<void> => {
  if (currentEtab.value === undefined)
    return

  dataState.value = 'LOADING'
  const response = await getRights(currentEtab.value)
  data.value = response
  dataState.value = response !== undefined ? 'LOADED' : 'ERROR'
})

function update(
  service: string,
  serviceRight: ServiceRight,
  toAdd: string[],
  toRemove: string[],
) {
  data.value = data.value?.map((d) => {
    if (d.service !== service)
      return d

    const rights = d.rights.map((right) => {
      if (right.role !== serviceRight.role)
        return right

      let currentMembers = right.currentMembers
      currentMembers = right.currentMembers.filter(({ id }) => !toRemove.includes(id))
      const membersToAdd: RightMember[] = toAdd.map((add) => {
        return {
          id: add,
          displayName: add,
          user: add.startsWith('F'),
          external: false,
        }
      })
      currentMembers.push(...membersToAdd)

      return { ...right, currentMembers }
    })

    return { ...d, rights }
  })
}
</script>

<template>
  <v-container>
    <h1>Gestion des droits d'administration</h1>

    <label for="tabs">Etablissement</label>
    <select
      id="tabs"
      v-model="currentEtab"
    >
      <option
        v-for="etab in etabs"
        :key="etab.id"
        :value="etab.id"
      >
        {{ etab.nom }} - {{ etab.type }} {{ etab.uai }}
      </option>
    </select>

    <ServicesRightsLayout
      :services-rights="data"
      :loading-state="dataState"
      @update="update"
    />
  </v-container>
</template>
