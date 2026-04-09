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
import type { Discipline, Filiere } from '@/types/index.ts'
import UserDiscipline from './UserDiscipline.vue'

const props = withDefaults(
  defineProps<{
    filiere: Filiere
    clickable?: boolean
  }>(),
  {
    clickable: false,
  },
)

const emit = defineEmits<{
  tagClick: [filiere: Filiere, discipline: Discipline]
}>()

function tagClick(discipline: Discipline): void {
  emit('tagClick', props.filiere, discipline)
}
</script>

<template>
  <div class="filiere-card">
    <h4>
      {{ filiere.libelleFiliere }}
    </h4>

    <ul>
      <li
        v-for="discipline in filiere.disciplines"
        :key="`discipline-tag-${discipline.code}`"
      >
        <UserDiscipline
          :discipline="discipline"
          :clickable="clickable"
          @tag-click="tagClick"
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

.filiere-card {
  display: flex;
  flex-direction: column;
  border-radius: 6px;
  border: 1px solid var(--#{$prefix}stroke);
  padding: 16px;

  > ul {
    @include unstyled-list;
    display: flex;
    flex-wrap: wrap;
    gap: 7px 8px;
  }
}
</style>
