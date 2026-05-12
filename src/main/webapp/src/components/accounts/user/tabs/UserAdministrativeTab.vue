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
import type { FunctionForm, User, UserStructure } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import UserAdditional from './administrative/UserAdditional.vue'
import UserFunctions from './administrative/UserFunctions.vue'

defineProps<{
  user?: User
}>()

const emit = defineEmits<{
  editFunction: [
    structure: UserStructure,
    fonction: FunctionForm | undefined,
  ]
}>()

const { t } = useI18n()

function editFunction(
  structure: UserStructure,
  fonction: FunctionForm | undefined,
): void {
  emit('editFunction', structure, fonction)
}
</script>

<template>
  <div>
    <div
      v-for="structure in user?.listeStructures"
      :key="`user-administrative-structure-${structure.id}`"
      class="structure-functions"
    >
      <h2>
        {{ structure.nom }}
        <span v-show="structure.type">
          {{ structure.type }}
          <span v-show="structure.uai">
            {{ structure.uai }}
          </span>
        </span>
      </h2>

      <div class="r-card">
        <header>
          <h3>
            {{ t('page.user.administrative.function.header', 2) }}
          </h3>
        </header>

        <div class="body">
          <UserFunctions
            :fonctions="structure.fonctions"
          />
        </div>
      </div>

      <UserAdditional
        :structure="structure"
        @edit-function="editFunction"
      />

      <div class="r-card">
        <header>
          <h3>
            {{ t('page.user.administrative.class', 2) }}
          </h3>
        </header>

        <div class="body">
          <ul v-if="structure.classes.length > 0">
            <li
              v-for="uClass in structure.classes"
              :key="`class-${uClass}`"
              class="tag-primary"
            >
              {{ uClass }}
            </li>
          </ul>
        </div>
      </div>

      <div class="r-card">
        <header>
          <h3>
            {{ t('page.user.administrative.educationalGroup', 2) }}
          </h3>
        </header>

        <div class="body">
          <ul
            v-if="structure.groupesPedagogiques.length > 0"
          >
            <li
              v-for="group in structure.groupesPedagogiques"
              :key="`educational-group${group}`"
              class="tag-primary"
            >
              {{ group }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.structure-functions {
  display: grid;
  gap: 16px;

  > h2 {
    margin-bottom: 0;

    > span {
      opacity: 0.6;
      font-size: var(--#{$prefix}font-size-sm);
    }
  }

  > :deep(.r-card) > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > ul {
      @include unstyled-list;
      display: flex;
      flex-wrap: wrap;
      gap: 7px 8px;
    }
  }

  @media (width >= map.get($grid-breakpoints, lg)) {
    grid-template-columns: repeat(2, 1fr);
    align-items: start;

    > h2 {
      grid-column: span 2;
    }
  }
}
</style>
