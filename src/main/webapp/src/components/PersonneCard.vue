<script setup lang="ts">
import { usePersonneStore } from '@/stores/personneStore.ts';
import type { enumValues } from '@/types/enumValuesType.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { getEtat, getIcon } from '@/utils/accountUtils.ts';
import { format, parseISO } from 'date-fns';
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
const tooltip = computed<string>(() => {
  const suppressDate = props.personne.dateSuppression
    ? format(parseISO(props.personne.dateSuppression), 'P')
    : undefined;
  const supressString = suppressDate ? ` (${t('person.status.deletingDate', { suppressDate })})` : '';
  return t(etat.value.i18n) + supressString;
});
</script>

<template>
  <v-card :variant="variant" tag="button" class="w-100" @click="initCurrentPersonne(personne.id, true)">
    <v-card-text class="d-flex align-center text-left pa-3">
      <v-tooltip :text="tooltip" location="bottom start">
        <template v-slot:activator="{ props }">
          <v-icon v-bind="props" :icon="icon" :color="etat.color" class="mr-2" />
        </template>
      </v-tooltip>
      {{ personne.cn }}
    </v-card-text>
  </v-card>
</template>
