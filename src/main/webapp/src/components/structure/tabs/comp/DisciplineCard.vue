<script setup lang="ts">
import type { FunctionUser } from '@/types/index.ts'
import { useId } from 'vue'
import UserLink from './UserLink.vue'

withDefaults(
  defineProps<{
    header?: boolean
    label?: string
    users: FunctionUser[]
  }>(),
  {
    header: false,
  },
)

const uid = useId()
</script>

<template>
  <div class="r-card">
    <header v-if="header">
      <h3>
        {{ label }}
      </h3>
      <span class="count">
        {{ users.length }}
      </span>
    </header>

    <div class="body">
      <ul>
        <li
          v-for="user in users"
          :key="`${uid}-user-${user.id}`"
        >
          <UserLink
            :user="user"
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

.r-card {
  gap: 16px;

  > header {
    display: inline-flex;
    align-items: center;
    gap: 8px;

    > h3 {
      margin-bottom: 0;
    }

    > .count {
      opacity: 0.6;
    }
  }

  > .body > ul {
    @include unstyled-list;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    gap: 8px;
  }
}
</style>
