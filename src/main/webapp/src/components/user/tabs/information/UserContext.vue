<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { usePersonne } from '@/composables/index.ts'
import { getIconDefinition } from '@/utils/index.ts'

defineProps<{
  user?: Personne
}>()

const { t } = useI18n()

const { schoolYear } = usePersonne()
</script>

<template>
  <div class="r-card info-card">
    <header>
      <h3>Contexte</h3>
    </header>

    <div class="body">
      <ul>
        <li>
          <h4>{{ t('person.information.schoolYear') }}</h4>
          <SafeEmptyData
            :value="schoolYear"
          />
        </li>
        <li>
          <h4>{{ t('person.information.source') }}</h4>
          <div class="user-account-type">
            <FontAwesomeIcon
              :icon="getIconDefinition(user?.source)"
              size="lg"
            />
            <SafeEmptyData
              :value="
                user?.source
                  ? t(`source.${user.source}`)
                  : undefined
              "
            />
          </div>
        </li>
        <li>
          <h4>{{ t('person.information.sourceModificationDate') }}</h4>
          <SafeEmptyData
            :value="
              user?.dateSourceModification
                ? format(user.dateSourceModification, 'P')
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

        > .user-account-type {
          display: inline-flex;
          align-items: center;
          gap: 4px;
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
