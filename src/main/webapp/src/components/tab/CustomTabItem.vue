<script setup lang="ts">
import type { RouteLocationNamedRaw } from 'vue-router';

defineProps<{
  id: number;
  title: string;
  link: RouteLocationNamedRaw;
  selected: boolean;
}>();

defineEmits<(event: 'close', payload: { id: number; selected: boolean }) => void>();
</script>

<template>
  <v-card
    variant="tonal"
    :color="selected ? 'primary' : 'default'"
    class="d-flex align-center mx-1 v-btn v-btn--size-default"
  >
    <router-link class="d-flex align-center text-decoration-none ms-2 h-100" :to="link">
      {{ title }}
    </router-link>
    <v-btn variant="text" density="comfortable" icon @click="$emit('close', { id, selected })">
      <v-icon icon="fas fa-xmark" size="x-small" color="secondary" />
    </v-btn>
  </v-card>
</template>

<style scoped lang="scss">
a {
  color: inherit;
}
</style>
