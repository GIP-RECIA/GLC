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
import type { Filiere } from '@/types'
import { faChevronDown } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, useId } from 'vue'
import Discipline from './Discipline.vue'

defineProps<{
  filiere: Filiere
}>()

const uid = useId()

const id = computed<string>(() => (
  `disclosure-${uid}`
))

const isExpanded = ref<boolean>(false)

function toggle(): void {
  isExpanded.value = !isExpanded.value
}
</script>

<template>
  <div class="disclosure">
    <header>
      <button
        :aria-expanded="isExpanded"
        :aria-controls="id"
        @click="toggle"
      >
        <h2>
          {{ filiere.libelleFiliere }}
        </h2>
        <span class="count">
          {{ filiere.disciplines.length + (filiere.personnesWithoutDiscipline.length > 0 ? 1 : 0) }}
        </span>
        <FontAwesomeIcon
          :icon="faChevronDown"
          :style="{
            rotate: isExpanded ? '180deg' : undefined,
          }"
          class="folded-indicator"
        />
      </button>
    </header>
    <ul
      v-if="isExpanded"
      :id="id"
      class="menu"
      :style="{
        display: isExpanded ? undefined : 'none',
      }"
    >
      <li
        v-for="discipline in filiere.disciplines"
        :key="`structure-filiere-${filiere.id}-discipline-${discipline.id}`"
      >
        <Discipline
          :label="discipline.disciplinePoste"
          :users="discipline.personnes"
        />
      </li>
      <li v-if="filiere.personnesWithoutDiscipline.length > 0">
        <Discipline
          label="SANS DISCIPLINE"
          :users="filiere.personnesWithoutDiscipline"
        />
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.disclosure {
  border-radius: 10px;

  > header {
    > button {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      // height: 60px;
      width: 100%;
      padding: 12px 16px;
      text-align: start;
      font-size: var(--#{$prefix}font-size-md);
      font-weight: bold;
      color: inherit;

      > h2 {
        margin-bottom: 0;
      }

      > .count {
        opacity: 0.6;
      }

      > .folded-indicator {
        font-size: 16px;
        margin: 8px;
        color: var(--#{$prefix}basic-black);
        transition: rotate 0.2s ease-in-out;
      }

      &:hover,
      &:focus-visible {
        outline: none;

        > .heading {
          color: var(--#{$prefix}primary);
        }
      }
    }
  }

  > .menu {
    @include unstyled-list;
    display: grid;
    gap: 16px;
    margin-top: 4px;
  }

  &:has(> header > button:focus-visible) {
    outline: 2px solid var(--#{$prefix}primary);
    outline-offset: 8px;
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > .menu {
      grid-template-columns: repeat(2, 1fr);

      > li {
        &:only-child,
        &:has(> .r-card > .body > ul > li:nth-child(10)) {
          grid-column: span 2;
        }
      }
    }
  }
}
</style>

<style lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.disclosure {
  > .menu > li {
    &:only-child,
    &:has(> .r-card > .body > ul > li:nth-child(10)) {
      > .r-card > .body > ul {
        @media (width >= map.get($grid-breakpoints, md)) {
          grid-template-columns: repeat(auto-fill, minmax(216px, 1fr));
        }
        @media (width >= map.get($grid-breakpoints, xxl)) {
          grid-template-columns: repeat(auto-fill, minmax(264px, 1fr));
        }
      }
    }
  }
}
</style>
