<script setup lang="ts">
import { usePersonneStore } from '@/stores/personneStore';
import type { enumValues } from '@/types/enumValuesType';
import type { SimplePersonne } from '@/types/personneType';
import { getEtat, getIcon } from '@/utils/accountUtils';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;

const props = defineProps<{
  personne: SimplePersonne;
  variant?: NonNullable<'flat' | 'text' | 'elevated' | 'tonal' | 'outlined' | 'plain'>;
}>();

const etat = computed<enumValues>(() => getEtat(props.personne.etat));
const icon = computed<string>(() => {
  if (etat.value.icon) return etat.value.icon;
  return getIcon(props.personne.source);
});
</script>

<template>
  <v-card :variant="variant" tag="button" class="w-100" @click="initCurrentPersonne(personne.id, true)">
    <v-card-text class="d-flex align-center text-left pa-3">
      <v-icon :icon="icon" :color="etat.color" :title="t(etat.i18n)" :alt="t(etat.i18n)" class="mr-2" />
      {{ personne.cn }}
    </v-card-text>
  </v-card>
</template>
