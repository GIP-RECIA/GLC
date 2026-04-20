<script setup lang="ts">
import type { Structure } from '@/types/index.ts'
import { faExclamationTriangle, faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'

defineProps<{
  structure?: Structure
}>()

const { t } = useI18n()
</script>

<template>
  <div class="structure-alerts">
    <h2>
      {{ t('page.structure.dashboard.alert.header') }}
    </h2>

    <ul
      v-if="structure?.alerts"
    >
      <li
        v-for="(alert, index) in structure.alerts"
        :key="`alert-${index}`"
        :class="[alert.type]"
      >
        <FontAwesomeIcon
          :icon="faExclamationTriangle"
          size="lg"
        />
        <div>
          <h3>{{ alert.title }}</h3>
          <p>{{ alert.text }}</p>

          <footer>
            <button
              class="btn-tertiary"
            >
              {{ t('button.add') }}
              <FontAwesomeIcon
                :icon="faPlus"
              />
            </button>
          </footer>
        </div>
      </li>
      <li
        v-if="
          structure.withoutFunctions
            && structure.withoutFunctions.length > 0
        "
        class="warning"
      >
        <FontAwesomeIcon
          :icon="faExclamationTriangle"
          size="lg"
        />
        <div>
          <h3>
            {{
              t(
                'page.structure.dashboard.alert.withoutFunctions.title',
                structure.withoutFunctions.length,
              )
            }}
          </h3>
          <p>
            {{
              t(
                'page.structure.dashboard.alert.withoutFunctions.text',
                { count: structure.withoutFunctions.length },
                structure.withoutFunctions.length,
              )
            }}
          </p>
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.structure-alerts {
  > ul {
    @include unstyled-list;
    display: grid;
    gap: 16px;

    > li {
      display: flex;
      gap: 8px;
      border-radius: 10px;
      box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
      padding: 16px;

      > svg {
        flex: 0 0 auto;
        margin-top: 3.5px;
      }

      > div {
        flex: 1 1 auto;

        > footer {
          display: flex;
          justify-content: flex-end;
          flex-wrap: wrap;
          gap: 6px;
          margin-top: 8px;
        }
      }

      &.success {
        > svg {
          color: #07bc0c;
        }
      }

      &.info {
        > svg {
          color: #3498db;
        }
      }

      &.warning {
        > svg {
          color: #f1c40f;
        }
      }

      &.error {
        > svg {
          color: #e74c3c;
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > ul {
      grid-template-columns: repeat(2, 1fr);
    }
  }
}
</style>
