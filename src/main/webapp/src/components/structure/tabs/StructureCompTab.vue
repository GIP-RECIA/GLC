<script setup lang="ts">
import type { Etablissement } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import { Etat, etatMap } from '@/types/enums/index.ts'
import Filiere from './comp/Filiere.vue'

defineProps<{
  structure?: Etablissement
}>()

const { t } = useI18n()

/* Filters */

enum Staff {
  Teaching = 'teaching',
  School = 'school',
  Collectivity = 'collectivity',
  Academic = 'academic',
}

const filters = [
  {
    id: 'staff',
    name: 'Personnel',
    type: 'checkbox',
    items: [
      {
        key: 'staff-all',
        value: 'Tous les personnels',
      },
      ...Object.values(Staff).map(staff => ({
        key: staff,
        value: t(`tab.${staff}`),
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
</script>

<template>
  <div>
    <r-filters
      :data="filters"
    />

    <Filiere
      v-for="filiere in structure?.filieres"
      :key="`structure-filiere-${filiere.id}`"
      :filiere="filiere"
    />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
