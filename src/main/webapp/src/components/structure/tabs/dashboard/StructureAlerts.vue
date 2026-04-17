<script setup lang="ts">
import type { Alert, Filiere, Structure } from '@/types/index.ts'
import { faExclamationTriangle, faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n'
import { useStructureStore } from '@/stores/index.ts'

defineProps<{
  structure?: Structure
}>()

const { t } = useI18n()

const structureStore = useStructureStore()
const { fonction } = storeToRefs(structureStore)

function getDiscipline(
  filieres: Filiere[] | undefined,
  code: string,
): string | undefined {
  const codes = code.split('-')
  if (!filieres)
    return undefined
  return filieres
    .find(filiere => filiere.codeFiliere === codes[0])
    ?.disciplines
    .find(discipline => discipline.code === codes[1])
    ?.disciplinePoste
}

function formatedAlert(
  alert: Alert,
): Alert & { class?: any, actions?: any } {
  if (!alert.title)
    return alert
  const data = alert.title.split('_')
  const discipline = getDiscipline(fonction.value?.filieres, data[1])
    ?? getDiscipline(fonction.value?.customMapping?.filieres, data[1])
  if (!discipline)
    return alert
  const actions: any = {}
  if (alert.action && fonction.value?.customMapping) {
    if (alert.title) {
      const doAlert = () => {
      }
      actions.click = () => doAlert()
    }
  }

  return {
    ...alert,
    title: t(
      'alert.minMax.title',
      {
        discipline,
        value: data[3],
      },
      Number.parseInt(data[3]),
    ),
    text: t(
      'alert.minMax.text',
      {
        minMax: t(`alert.minMax.${data[0]}`),
        required: data[2],
      },
    ),
    class: [{
      clicable: alert.action && fonction.value?.customMapping,
    }],
    actions,
  }
}
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
        v-for="(alert, index) in structure.alerts.map(formatedAlert)"
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
