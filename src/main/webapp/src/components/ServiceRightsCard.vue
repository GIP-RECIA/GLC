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
            class="tag-category"
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
