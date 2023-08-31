<script setup lang="ts">
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  tags: Array<{ id: any; i18n: string }>;
}>();

const emit = defineEmits<(event: 'update:selected', payload: Array<any>) => void>();

const filter = (payload: Array<number>) => {
  const ids = props.tags.filter((_, index) => payload.includes(index)).map((tag) => tag.id);
  emit('update:selected', ids);
};
</script>

<template>
  <v-chip-group column multiple selected-class="text-primary" @update:model-value="filter">
    <v-chip v-for="(tag, index) in tags" :key="index" rounded>{{ t(tag.i18n) }}</v-chip>
  </v-chip-group>
</template>
