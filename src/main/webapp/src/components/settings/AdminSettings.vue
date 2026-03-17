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
import type { Etablissement } from '@/types/index.ts'
import { getYear } from 'date-fns'
import { computed } from 'vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'

const props = defineProps<{
  etab?: Etablissement
}>()

const schoolYear = computed<string | undefined>(() => {
  if (!props.etab)
    return

  const year = getYear(props.etab.anneeScolaire)

  return `${year}/${year + 1}`
})
</script>

<template>
  <div class="r-card info-card">
    <header>
      <h3>Administration</h3>
    </header>

    <div class="body">
      <ul>
        <li>
          <h4>Etat</h4>
          <SafeEmptyData
            :value="etab?.etat"
          />
        </li>
        <li>
          <h4>Etat alimentation</h4>
          <SafeEmptyData
            :value="etab?.etatAlim"
          />
        </li>
        <li>
          <h4>Source</h4>
          <SafeEmptyData
            :value="etab?.source"
          />
        </li>
        <li>
          <h4>Année scolaire</h4>
          <SafeEmptyData
            :value="schoolYear"
          />
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.info-card {
  display: flex;
  flex-direction: column;

  > .body {
    > ul {
      @include unstyled-list;
      display: flex;
      flex-direction: column;
      gap: 16px;

      > li {
        > h4 {
          margin-bottom: 4px;
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, xl)) {
    > .body > ul {
      display: grid;
      grid-template-columns: repeat(2, 1fr);

      > .full-width {
        grid-column: span 2;
      }
    }
  }
}
</style>
