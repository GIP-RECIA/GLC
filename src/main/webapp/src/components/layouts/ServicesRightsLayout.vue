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
import type { ServiceRight, ServiceRights } from '@/types/index.ts'
import { ref } from 'vue'
import { toast } from 'vue3-toastify'
import ServiceRightsCard from '@/components/ServiceRightsCard.vue'
import { updateRight } from '@/services/api/index.ts'
import ManageServiceRightsDialog from '../dialogs/ManageServiceRightsDialog.vue'

defineProps<{
  servicesRights: ServiceRights[] | undefined
}>()

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
) {
  const response = await updateRight(
    28,
    service,
    serviceRight.role,
    toAdd,
    toRemove,
  )
  if (response)
    toast.success('OK')
}
</script>

<template>
  <div class="services-grid">
    <div
      v-for="serviceRights in servicesRights"
      :key="serviceRights.service"
    >
      <ServiceRightsCard
        :service-rights="serviceRights"
        @edit="edit"
      />
    </div>
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

.services-grid {
  display: grid;
  gap: 24px;
}

@media (width >= map.get($grid-breakpoints, md)) {
  .services-grid {
    gap: 16px;
    grid-template-columns: repeat(auto-fill, minmax(512px, 1fr));
  }
}
</style>
