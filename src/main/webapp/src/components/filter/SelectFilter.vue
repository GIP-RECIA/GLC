<script setup lang="ts">
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  modelValue: Array<number | string>;
  items: Array<{ i18n: string; value: number | string; color?: string }>;
}>();

const emit = defineEmits<(event: 'update:modelValue', payload: Array<number | string>) => void>();

const modelValue = computed<Array<number | string>>({
  get() {
    return props.modelValue;
  },
  set(payload: unknown) {
    emit('update:modelValue', payload as Array<number | string>);
  },
});
</script>

<template>
  <v-select
    v-model="modelValue"
    :items="items"
    rounded="xl"
    variant="solo"
    density="compact"
    flat
    chips
    multiple
    hide-details
    class="select-filter"
  >
    <template #prepend-inner>
      <v-badge v-if="modelValue.length > 0" :content="modelValue.length" color="info">
        <v-icon icon="fas fa-filter" />
      </v-badge>
      <v-icon v-else icon="fas fa-filter" />
    </template>
    <template #chip />
    <template #item="{ props, item }">
      <v-list-item v-bind="props" title="">
        <div class="d-flex flex-row align-center">
          <v-icon v-if="item.raw.color" icon="fas fa-circle" :color="item.raw.color" class="mr-2" />
          <div>{{ t(item.raw.i18n) }}</div>
        </div>
      </v-list-item>
    </template>
  </v-select>
</template>

<style scoped lang="scss">
.select-filter {
  max-width: fit-content;
}
</style>
