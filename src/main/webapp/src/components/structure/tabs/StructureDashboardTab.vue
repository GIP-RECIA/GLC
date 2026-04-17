<script setup lang="ts">
import type { Structure } from '@/types/index.ts'
import StructureAccountsStates from './dashboard/StructureAccountsStates.vue'
import StructureAlerts from './dashboard/StructureAlerts.vue'

defineProps<{
  structure?: Structure
}>()
</script>

<template>
  <div>
    <StructureAlerts
      :structure="structure"
    />

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
