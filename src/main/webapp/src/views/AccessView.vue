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
import ManageServiceRightsDialog from '@/components/access/dialogs/ManageServiceRightsDialog.vue'
import ServiceRightsCard from '@/components/access/ServiceRightsCard.vue'
import PageLayout from '@/components/PageLayout.vue'
import StructureSearch from '@/components/StructureSearch.vue'
import { getRights, updateRight } from '@/services/api/index.ts'
import { useStructureStore } from '@/stores/index.ts'

const structureStore = useStructureStore()
structureStore.init()
const { etabs } = storeToRefs(structureStore)

/* Data */

const data = ref<ServiceRights[]>()

function updateData(
  service: string,
  serviceRight: ServiceRight,
  toAdd: string[],
  toRemove: string[],
): void {
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

/* Structure */

const selectedEtab = ref<number | undefined>(
  etabs.value
    ? etabs.value[0]?.id
    : undefined,
)

watchEffect(async (): Promise<void> => {
  if (selectedEtab.value === undefined)
    return

  data.value = await getRights(selectedEtab.value)
})

/* Dialog */

const dialogState = ref<boolean>(false)

const serviceRight = ref<Pick<ServiceRights, 'service'> & ServiceRight>()

function edit(
  service: string,
  right: ServiceRight,
): void {
  serviceRight.value = {
    service,
    ...right,
  }
  dialogState.value = true
}

async function save(
  service: string,
  serviceRight: ServiceRight,
  toAdd: string[],
  toRemove: string[],
): Promise<void> {
  if (!selectedEtab.value)
    return

  const response = await updateRight({
    id: selectedEtab.value,
    service,
    role: serviceRight.role,
    membersToAdd: toAdd,
    membersToRemove: toRemove,
  })
  if (response) {
    updateData(
      service,
      serviceRight,
      toAdd,
      toRemove,
    )
  }
}
</script>

<template>
  <div class="container">
    <PageLayout
      title="Gestion des accès aux services"
    >
      <StructureSearch
        v-model="selectedEtab"
        :search-list="etabs"
        variant="solo"
      />

      <div>
        <h2>Services</h2>

        <div class="services-grid">
          <ServiceRightsCard
            v-for="serviceRights in data"
            :key="serviceRights.service"
            :service-rights="serviceRights"
            @edit="edit"
          />
        </div>
      </div>
    </PageLayout>
  </div>

  <ManageServiceRightsDialog
    v-model="dialogState"
    :service-right="serviceRight"
    @update:model-value="dialogState = false"
    @save="save"
  />
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

.services-grid {
  display: grid;
  gap: 24px;

  @media (width >= map.get($grid-breakpoints, md)) {
    gap: 16px;
    grid-template-columns: repeat(auto-fill, minmax(512px, 1fr));
  }
}
</style>
