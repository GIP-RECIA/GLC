<script setup lang="ts">
import type { Etablissement } from '@/types/index.ts'
import { faFileExport, faLockOpen } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import { CategoriePersonne, categoriePersonneMap, Etat, etatMap } from '@/types/enums/index.ts'

defineProps<{
  structure?: Etablissement
}>()

const { t } = useI18n()

/* Filters */

const filters = [
  {
    id: 'source',
    name: 'Source',
    type: 'checkbox',
    items: [
      {
        key: 'source-all',
        value: 'Toutes les sources',
      },
      {
        key: 'annu',
        value: 'Annuaire',
      },
      {
        key: 'local',
        value: 'Comple local',
      },
    ],
  },
  {
    id: 'profil',
    name: 'Profil',
    type: 'checkbox',
    items: [
      {
        key: 'profil-all',
        value: 'Tous les profils',
      },
      ...Object.values(CategoriePersonne).map(cat => ({
        key: cat,
        value: t(categoriePersonneMap[cat].i18n),
      })),
    ],
  },
  {
    id: 'state',
    name: 'État',
    type: 'checkbox',
    items: [
      {
        key: 'state-all',
        value: 'Tous les états',
      },
      ...Object.values(Etat).map(etat => ({
        key: etat,
        value: t(etatMap[etat].i18n),
      })),
    ],
  },
]

/* Actions */

function onUnlock(): void {

}

function onExport(): void {

}
</script>

<template>
  <div>
    <r-filters
      :data="filters"
    />

    <div class="accounts-actions">
      <h2 class="sr-only">
        {{ t('page.structure.actions') }}
      </h2>

      <ul>
        <li>
          <button
            type="button"
            class="btn-primary small"
            @click="onUnlock"
          >
            {{ t('button.unlock') }}
            <FontAwesomeIcon
              :icon="faLockOpen"
            />
          </button>
        </li>
        <li>
          <button
            type="button"
            class="btn-primary small"
            @click="onExport"
          >
            {{ t('button.export') }}
            <FontAwesomeIcon
              :icon="faFileExport"
            />
          </button>
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

.accounts-actions {
  flex: 0 1 auto;
  min-width: 320 - 2 * 16px;

  > ul {
    @include unstyled-list;
    display: flex;
    flex-wrap: wrap;
    justify-content: end;
    gap: 8px;
  }
}
</style>
