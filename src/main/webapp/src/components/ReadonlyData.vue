<script setup lang="ts">
defineProps<{
  label: string;
  value?: string;
}>();
</script>

<template>
  <div class="mb-3">
    <b>{{ label }}</b>
    <slot />
    <div v-if="!$slots.default">
      {{ value ? value : '-' }}
    </div>
  </div>
</template>
