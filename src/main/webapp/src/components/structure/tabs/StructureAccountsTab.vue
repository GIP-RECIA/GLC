<script setup lang="ts">
import type { Etablissement } from '@/types/index.ts'
import { faFileExport } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import { CategoriePersonne, Etat } from '@/types/enums/index.ts'
import { getCategoriePersonne, getEtat } from '@/utils/index.ts'

defineProps<{
  structure?: Etablissement
}>()

const { t } = useI18n()

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
        value: t(getCategoriePersonne(cat).i18n),
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
        value: t(getEtat(etat).i18n),
      })),
    ],
  },
]
</script>

<template>
  <div>
    <r-filters
      :data="filters"
    />

    <ul>
      <li>
        <button
          type="button"
          class="btn-primary small"
        >
          Exporter
          <FontAwesomeIcon
            :icon="faFileExport"
          />
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
