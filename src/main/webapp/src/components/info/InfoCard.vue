<script setup lang="ts">
import type { enumValues } from '@/types/enumValuesType.ts';
import type { Etat } from '@/types/enums/Etat.ts';
import { getEtat } from '@/utils/accountUtils.ts';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  etat: Etat;
  value: number;
  variant?: NonNullable<'flat' | 'text' | 'elevated' | 'tonal' | 'outlined' | 'plain'>;
}>();

const data = computed<{ etat: enumValues; icon: string }>(() => {
  const etat = getEtat(props.etat);
  const icon = etat.icon ? etat.icon : 'fas fa-user';

  return { etat, icon };
});
</script>

<template>
  <v-card :variant="variant">
    <v-card-text class="d-flex align-center justify-space-between text-center pa-3">
      <div class="w-100">
        <div class="text-h5">{{ value }}</div>
        <div class="text-caption text-medium-emphasis">{{ t(data.etat.i18n) }}</div>
      </div>
      <v-icon :icon="data.icon" :color="data.etat.color" class="me-2" :size="40" />
    </v-card-text>
  </v-card>
</template>
