<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  tags: Array<{ id: number | string; i18n: string }>;
}>();

const emit = defineEmits<(event: 'update:selected', payload: Array<number | string>) => void>();

const filter = (payload: unknown) => {
  const ids: Array<number | string> = props.tags
    .filter((_, index) => (payload as Array<number>).includes(index))
    .map((tag) => tag.id);
  emit('update:selected', ids);
};
</script>

<template>
  <v-chip-group column multiple selected-class="text-primary" @update:model-value="filter">
    <v-chip v-for="(tag, index) in tags" :key="index" :text="t(tag.i18n)" rounded />
  </v-chip-group>
</template>
