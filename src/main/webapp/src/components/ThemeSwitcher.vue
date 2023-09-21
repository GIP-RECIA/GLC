<script setup lang="ts">
import { usePreferredDark } from '@vueuse/core';
import { watch } from 'vue';
import { useTheme } from 'vuetify';

const theme = useTheme();
const isDark = usePreferredDark();

watch(isDark, (newValue) => {
  theme.global.name.value = !newValue ? 'light' : 'dark';
});

const toggleTheme = () => {
  theme.global.name.value = theme.global.current.value.dark ? 'light' : 'dark';
};
</script>

<template>
  <v-btn variant="text" density="comfortable" class="ma-0" icon @click="toggleTheme">
    <v-icon :icon="theme.global.name.value == 'light' ? 'fas fa-moon' : 'fas fa-sun'" size="x-small" />
  </v-btn>
</template>
