<script setup lang="ts">
import type { Etablissement } from '@/types/index.ts'
import StructureAccountsStates from './dashboard/StructureAccountsStates.vue'

defineProps<{
  structure?: Etablissement
}>()
</script>

<template>
  <div>
    <div>
      <h2>Alertes</h2>
    </div>

    <StructureAccountsStates
      :structure="structure"
    />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
