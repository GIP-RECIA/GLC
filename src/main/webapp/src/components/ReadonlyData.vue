<script setup lang="ts">
defineProps<{
  label: string;
  value?: string;
}>();
</script>

<template>
  <div class="mb-3">
    <b>{{ label }}</b>
    <br />
    {{ value ? value : '-' }}
  </div>
</template>
