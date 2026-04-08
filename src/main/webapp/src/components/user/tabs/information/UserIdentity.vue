<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
import { format } from 'date-fns'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'

defineProps<{
  user?: Personne
}>()

const { t } = useI18n()
</script>

<template>
  <div class="r-card info-card">
    <header>
      <h3>Informations personnelles</h3>
    </header>

    <div class="body">
      <ul>
        <li>
          <h4>{{ t('person.information.civility') }}</h4>
          <SafeEmptyData
            :value="user?.civilite"
          />
        </li>
        <li>
          <h4>{{ t('person.information.lastName') }}</h4>
          <SafeEmptyData
            :value="user?.patronyme"
          />
        </li>
        <li>
          <h4>{{ t('person.information.firstName') }}</h4>
          <SafeEmptyData
            :value="user?.givenName"
          />
        </li>
        <li>
          <h4>{{ t('person.information.birthDate') }}</h4>
          <SafeEmptyData
            :value="
              user?.dateNaissance
                ? format(user.dateNaissance, 'P')
                : undefined
            "
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
