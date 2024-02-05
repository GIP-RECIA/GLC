<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

defineProps<{
  items: Array<{ id: number | string; i18n: string }>;
}>();

const modelValue = defineModel<Array<number | string>>();
</script>

<template>
  <v-chip-group v-model="modelValue" column multiple selected-class="text-primary">
    <v-chip v-for="(tag, index) in items" :key="index" :value="tag.id" :text="t(tag.i18n)" rounded />
  </v-chip-group>
</template>
