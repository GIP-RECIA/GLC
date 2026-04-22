<script setup lang="ts">
import type { User } from '@/types/index.ts'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format, getYear } from 'date-fns'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { getIconDefinition } from '@/utils/index.ts'

const props = defineProps<{
  user?: User
}>()

const { t } = useI18n()

const schoolYear = computed<string | undefined>(() => {
  if (!props.user)
    return

  const year = getYear(props.user.anneeScolaire)

  return `${year}/${year + 1}`
})
</script>

<template>
  <div class="r-card info-card">
    <header>
      <h3>
        {{ t('page.user.info.context.header') }}
      </h3>
    </header>

    <div class="body">
      <ul>
        <li>
          <h4>
            {{ t('page.user.info.context.schoolYear') }}
          </h4>
          <SafeEmptyData
            :value="schoolYear"
          />
        </li>
        <li>
          <h4>
            {{ t('page.user.info.context.source') }}
          </h4>
          <div class="user-account-type">
            <FontAwesomeIcon
              :icon="getIconDefinition(user?.local)"
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
          <h4>
            {{ t('page.user.info.context.sourceModificationDate') }}
          </h4>
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
