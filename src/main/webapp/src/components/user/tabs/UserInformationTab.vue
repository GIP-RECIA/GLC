<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
// import { useI18n } from 'vue-i18n'
import UserAccount from '../UserAccount.vue'
import UserContext from '../UserContext.vue'
import UserHeader from '../UserHeader.vue'
import UserIdentity from '../UserIdentity.vue'

defineProps<{
  user?: Personne
}>()

// const { t } = useI18n()
</script>

<template>
  <div>
    <div class="general-information">
      <h2>Informations générales</h2>

      <div class="info-container">
        <UserHeader
          :user="user"
        />

        <UserIdentity
          :user="user"
        />

        <UserAccount
          :user="user"
        />

        <UserContext
          :user="user"
        />
      </div>
    </div>

    <div class="structure-information">
      <h2>Structures</h2>

      <ul v-show="false">
        <li
          v-for="item in []"
          :key="item"
        >
          <div class="r-card" />
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

.general-information {
  @media (width >= map.get($grid-breakpoints, md)) {
    .header-card {
      grid-column: span 2;
    }
  }
}

.structure-information {
  > ul {
    @include unstyled-list;
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
  }
}
</style>
