<script setup lang="ts">
defineProps<{
  indeterminate?: boolean
  className?: string
}>()
</script>

<template>
  <input
    type="checkbox"
    :class="`${className} cursor-pointer`"
    :indeterminate="indeterminate"
    v-bind="$attrs"
  >
</template>
