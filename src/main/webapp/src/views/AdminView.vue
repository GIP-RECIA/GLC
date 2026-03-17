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
import PageLayout from '@/components/PageLayout.vue'
import StructureSearch from '@/components/search/structure/StructureSearch.vue'
import { getRights } from '@/services/api/index.ts'
import { useStructureStore } from '@/stores/index.ts'

const structureStore = useStructureStore()
structureStore.init()
const { etabs } = storeToRefs(structureStore)

const selectedEtab = ref<number | undefined>(
  etabs.value
    ? etabs.value[0]?.id
    : undefined,
)

const data = ref<ServiceRights[]>()

watchEffect(async (): Promise<void> => {
  if (selectedEtab.value === undefined)
    return

  data.value = await getRights(selectedEtab.value)
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
  <div class="container">
    <PageLayout
      title="Gestion des droits d'administration"
    >
      <StructureSearch
        v-model="selectedEtab"
        :search-list="etabs"
        variant="solo"
      />

      <ServicesRightsLayout
        :services-rights="data"
        loading-state="LOADED"
        @update="update"
      />
    </PageLayout>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.container {
  margin-top: 32px;
  margin-bottom: 40px;

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;
  }
}
</style>
