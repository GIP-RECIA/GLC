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

defineProps<{
  serviceRights: ServiceRights
}>()

defineEmits<{
  edit: [service: string, right: ServiceRight]
}>()
</script>

<template>
  <div class="service-card">
    <header>
      <h2>
        {{ serviceRights.service }}
      </h2>
    </header>

    <div class="body">
      <div
        v-for="right in serviceRights.rights"
        :key="right.role"
        class="role-card"
      >
        <header>
          <h3>
            {{ right.description }}
          </h3>
        </header>

        <ul>
          <li
            v-for="member in right.currentMembers"
            :key="member.id"
            class="tag-primary"
          >
            {{ member.displayName }}
          </li>
        </ul>

        <footer>
          <button
            class="btn-primary small"
            @click="$emit('edit', serviceRights.service, right)"
          >
            Modifier
            <font-awesome-icon icon="fas fa-pen" />
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.service-card {
  display: flex;
  flex-direction: column;
  border-radius: 10px;
  box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
  padding: 16px;
  background-color: $white;

  > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > .role-card {
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
        padding-bottom: 16px;

        > li {
          background-color: var(--#{$prefix}primary);
        }
      }

      > footer {
        display: flex;
        justify-content: flex-end;
        margin-top: 24px;
      }
    }
  }
}
</style>
