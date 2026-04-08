<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { usePersonne } from '@/composables/index.ts'

defineProps<{
  user?: Personne
}>()

const { t } = useI18n()

const { login } = usePersonne()
</script>

<template>
  <div class="r-card info-card">
    <header>
      <h3>Compte & accès</h3>
    </header>

    <div class="body">
      <ul>
        <li v-show="user?.uid">
          <h4>UID</h4>
          <SafeEmptyData
            :value="user?.uid"
          />
        </li>
        <li>
          <h4>{{ t('person.information.login') }}</h4>
          <SafeEmptyData
            :value="login.i18n"
          />
        </li>
        <li>
          <h4>{{ t('person.information.email') }}</h4>
          <SafeEmptyData
            :value="user?.email"
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
