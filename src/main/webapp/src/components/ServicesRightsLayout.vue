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
import ManageServiceRightsDialog from './ManageServiceRightsDialog.vue'

defineProps<{
  servicesRights: ServiceRights[] | undefined
  loadingState: NonNullable<'UNLOADED' | 'LOADING' | 'LOADED' | 'ERROR'>
}>()

const emit = defineEmits<{
  update: [
    service: string,
    right: ServiceRight,
    toAdd: string [],
    toRemove: string [],
  ]
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
  const response = await updateRight({
    id: 28,
    service,
    role: serviceRight.role,
    membersToAdd: toAdd,
    membersToRemove: toRemove,
  })
  if (response) {
    toast.success('OK')
    emit(
      'update',
      service,
      serviceRight,
      toAdd,
      toRemove,
    )
  }
}
</script>

<template>
  <template v-if="loadingState !== 'ERROR'">
    <div class="services-grid">
      <template v-if="loadingState === 'LOADED'">
        <div
          v-for="serviceRights in servicesRights"
          :key="serviceRights.service"
        >
          <ServiceRightsCard
            :service-rights="serviceRights"
            @edit="edit"
          />
        </div>
      </template>
      <template v-else-if="loadingState === 'LOADING'">
        <div
          v-for="skeleton in [1, 2, 3]"
          :key="skeleton"
          class="skeleton"
        />
      </template>
    </div>

    <ManageServiceRightsDialog
      v-model="dialogState"
      :service-right="serviceRight"
      @update:model-value="dialogState = false"
      @save="save"
    />
  </template>
  <div v-else class="error">
    <p>
      Une erreur s'est produite lors du chargement
    </p>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;

.services-grid {
  display: grid;
  gap: 24px;

  > .skeleton {
    width: 100%;
    height: 300px;
    border-radius: 10px;
    background: linear-gradient(90deg, #e9e9e9 30%, #f6f6f6 50%, #e9e9e9 70%);
    background-size: 200% 100%;
    animation: shimmer 1.5s infinite linear;
  }
}

.error {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  > svg {
    font-size: 38px;
    margin-bottom: 14px;
    opacity: 0.1;
  }

  > p {
    white-space-collapse: preserve-breaks;
  }
}

@media (width >= map.get($grid-breakpoints, md)) {
  .services-grid {
    gap: 16px;
    grid-template-columns: repeat(auto-fill, minmax(512px, 1fr));
  }
}
</style>
