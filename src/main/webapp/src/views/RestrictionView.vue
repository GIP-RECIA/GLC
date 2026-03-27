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
import type { StructureRestriction } from '@/types/index.ts'
import { storeToRefs } from 'pinia'
import { ref, watchEffect } from 'vue'
import PageLayout from '@/components/PageLayout.vue'
import CloseRestrictions from '@/components/restrictions/CloseRestrictions.vue'
import GlobalRestrictions from '@/components/restrictions/GlobalRestrictions.vue'
import OpenRestrictions from '@/components/restrictions/OpenRestrictions.vue'
import StructureSearch from '@/components/search/structure/StructureSearch.vue'
import { getRestrictions } from '@/services/api/index.ts'
import { useStructureStore } from '@/stores/index.ts'

const structureStore = useStructureStore()
structureStore.init()
const { etabs } = storeToRefs(structureStore)

const selectedEtab = ref<number | undefined>(
  etabs.value
    ? etabs.value[0]?.id
    : undefined,
)

const data = ref<StructureRestriction | undefined>()

watchEffect(async (): Promise<void> => {
  if (selectedEtab.value === undefined)
    return

  data.value = await getRestrictions(selectedEtab.value)
})

const isChildEdit = ref<boolean>(false)

function setChildEditState(state: boolean): void {
  isChildEdit.value = state
}

function update(restrictions: StructureRestriction): void {
  data.value = restrictions
}
</script>

<template>
  <div class="container">
    <PageLayout
      title="Paramétrage des dates"
    >
      <StructureSearch
        v-model="selectedEtab"
        :search-list="etabs"
        variant="solo"
      />

      <div>
        <h2>Période de rentrée</h2>

        <div class="info-container">
          <GlobalRestrictions
            :etab-id="selectedEtab"
            :restrictions="data"
            :disable-edit="isChildEdit"
            @edit="setChildEditState"
            @update="update"
          />

          <template v-if="!data || data.enabled">
            <CloseRestrictions
              :restrictions="data"
            />

            <OpenRestrictions
              :etab-id="selectedEtab"
              :restrictions="data"
              :disable-edit="isChildEdit"
              class="full-width"
              @edit="setChildEditState"
              @update="update"
            />
          </template>
        </div>
      </div>
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

.info-container {
  display: flex;
  flex-direction: column;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    align-items: start;

    > .full-width {
      grid-column: span 2;
    }
  }
}
</style>
