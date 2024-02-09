<script setup lang="ts">
import type { enumValues } from '@/types/enumValuesType.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { getEtat, getIcon } from '@/utils/accountUtils.ts';
import { concatenate } from '@/utils/stringUtils.ts';
import { computed } from 'vue';

const props = defineProps<{
  personne: SimplePersonne;
}>();

const etat = computed<enumValues>(() => getEtat(props.personne.etat));
</script>

<template>
  <v-list-item :subtitle="concatenate([personne.email, personne.uid], ' - ')">
    <template #title>{{ personne.cn }}</template>
    <template #prepend>
      <v-icon :icon="getIcon(personne.source)" :color="etat.color" />
    </template>
  </v-list-item>
</template>
