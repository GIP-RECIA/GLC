<script setup lang="ts">
import { toRefs } from "vue";

const props = defineProps<{
  modelValue: boolean;
  title: string;
}>();

defineEmits<(event: "update:modelValue", payload: boolean) => void>();

const { modelValue } = toRefs(props);
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024">
    <v-card>
      <v-toolbar color="rgba(0, 0, 0, 0)">
        <v-toolbar-title class="text-h6">{{ title }}</v-toolbar-title>
        <template v-slot:append>
          <v-btn
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            @click="$emit('update:modelValue', false)"
          />
        </template>
      </v-toolbar>
      <v-card-text>
        <slot />
      </v-card-text>
      <v-card-actions class="d-flex justify-end">
        <slot name="footer" />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
