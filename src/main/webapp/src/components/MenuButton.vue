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
import { computed, ref, useId } from 'vue'

withDefaults(
  defineProps<{
    items: {
      uid: string | number
      name: string
    }[]
    disabled?: boolean
    aLabel?: string
    btnClass?: string
  }>(),
  {
    disabled: false,
  },
)

defineEmits<{
  'update:model-value': [uid: string | number]
}>()

const uid = useId()

const id = computed<string>(() => (
  `dropdown-${uid}`
))

const isExpanded = ref<boolean>(false)

function toggle(): void {
  isExpanded.value = !isExpanded.value
}
</script>

<template>
  <div class="dropdown">
    <button
      :aria-expanded="isExpanded"
      :aria-controls="id"
      :aria-label="aLabel"
      :disabled="disabled"
      :class="btnClass"
      @click="() => toggle()"
    >
      <slot />
    </button>
    <ul
      :id="id"
      :style="{
        display: isExpanded ? undefined : 'none',
      }"
    >
      <li
        v-for="item in items"
        :key="item.uid"
      >
        <button
          @click="() => {
            toggle()
            $emit('update:model-value', item.uid)
          }"
        >
          {{ item.name }}
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.dropdown {
  position: relative;
  width: fit-content;

  > ul {
    @include unstyled-list;
    position: absolute;
    top: calc(100% + 6px);
    right: 0;
    min-width: 100%;
    max-width: calc(100vw - 32px);
    background-color: var(--#{$prefix}body-bg);
    border-radius: 8px;
    box-shadow: var(--#{$prefix}shadow-strong) HEXToRGBA($black, 0.1);
    overflow: hidden;
    z-index: 1000;

    > li > button {
      @include unstyled-link;
      display: inline-flex;
      align-items: center;
      padding: 14px 12px;
      text-wrap: nowrap;
      font-size: var(--#{$prefix}font-size-sm);
      width: 100%;

      &:hover,
      &:focus-visible {
        font-weight: 500;
        background-color: var(--#{$prefix}hover);
        outline: none;
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > ul > li:not(:last-child) {
      border-bottom: 1px solid HEXToRGBA($black, 0.1);
    }
  }
}
</style>
