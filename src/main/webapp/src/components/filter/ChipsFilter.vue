<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  modelValue?: Array<number | string>;
  items: Array<{ id: number | string; i18n: string }>;
}>();

const emit = defineEmits<(event: 'update:modelValue', payload: Array<number | string>) => void>();

const modelValue = computed<Array<number | string>>({
  get() {
    return props.modelValue ? props.modelValue : [];
  },
  set(newValue) {
    emit('update:modelValue', newValue);
  },
});
</script>

<template>
  <v-chip-group v-model="modelValue" column multiple selected-class="text-primary">
    <v-chip v-for="(tag, index) in items" :key="index" :value="tag.id" :text="t(tag.i18n)" rounded />
  </v-chip-group>
</template>
