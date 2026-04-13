<script setup lang="ts">
import type { Etablissement } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import { Etat } from '@/types/enums/index.ts'
import { getEtat } from '@/utils/index.ts'

defineProps<{
  structure?: Etablissement
}>()

const { t } = useI18n()

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
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
