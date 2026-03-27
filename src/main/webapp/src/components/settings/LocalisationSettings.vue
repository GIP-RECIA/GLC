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
import SafeEmptyData from '@/components/SafeEmptyData.vue'

defineProps<{
  etab?: Etablissement
}>()
</script>

<template>
  <div class="r-card info-card">
    <header>
      <h3>Localisation</h3>
    </header>

    <div class="body">
      <ul>
        <li class="full-width">
          <h4>Adresse</h4>
          <SafeEmptyData
            :value="etab?.adresse.adresse"
          />
        </li>
        <li>
          <h4>Code postal</h4>
          <SafeEmptyData
            :value="etab?.adresse.codePostal"
          />
        </li>
        <li>
          <h4>Ville</h4>
          <SafeEmptyData
            :value="etab?.adresse.ville"
          />
        </li>
        <li>
          <h4>Boite postal</h4>
          <SafeEmptyData
            :value="etab?.adresse.boitePostale"
          />
        </li>
        <li>
          <h4>Pays</h4>
          <SafeEmptyData
            :value="etab?.adresse.pays"
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
