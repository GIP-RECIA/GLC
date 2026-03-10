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
import { onMounted, ref } from 'vue'
import ServicesRightsLayout from '@/components/layouts/ServicesRightsLayout.vue'
import { getRights } from '@/services/api/index.ts'

const data = ref<ServiceRights[]>()

onMounted(async () => {
  data.value = await getRights(28)
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
    <h1>Gestion des accès aux services</h1>

    <ServicesRightsLayout
      :services-rights="data"
      @update="update"
    />
  </v-container>
</template>
