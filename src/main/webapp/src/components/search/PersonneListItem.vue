<script setup lang="ts">
import type { enumValues } from '@/types/enumValuesType';
import type { SimplePersonne } from '@/types/personneType';
import { getEtat, getIcon } from '@/utils/accountUtils';
import { computed } from 'vue';

const props = defineProps<{
  personne: SimplePersonne;
}>();

const etat = computed<enumValues>(() => getEtat(props.personne.etat));
const subtitle = computed<string>(() => {
  let str = '';
  if (props.personne.email) str = props.personne.email;
  if (str != '') str += ' - ';
  if (props.personne.uid) str += props.personne.uid;

  return str;
});
</script>

<template>
  <v-list-item :subtitle="subtitle">
    <template #title>
      {{ personne.cn }}
    </template>
    <template #prepend>
      <v-icon :icon="getIcon(personne.source)" :color="etat.color" />
    </template>
  </v-list-item>
</template>
