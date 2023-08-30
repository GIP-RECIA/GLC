<script setup lang="ts">
import { computed } from "vue";

const props = defineProps<{
  modelValue: boolean;
  title: string;
  showXmark?: boolean;
}>();

defineEmits<(event: "update:modelValue", payload: boolean) => void>();

const modelValue = computed<boolean>({
  get() {
    return props.modelValue;
  },
  set() {},
});
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ title }}</v-toolbar-title>
        <template v-slot:append>
          <v-btn
            v-if="!showXmark == true"
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            @click="$emit('update:modelValue', false)"
          />
        </template>
      </v-toolbar>
      <v-card-text class="py-0">
        <slot />
      </v-card-text>
      <v-card-actions v-if="$slots.footer" class="d-flex justify-end">
        <slot name="footer" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
