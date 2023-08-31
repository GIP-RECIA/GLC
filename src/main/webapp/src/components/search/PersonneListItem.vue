<script setup lang="ts">
import type { enumValues } from '@/types/enumValuesType';
import { getEtat } from '@/types/enums/Etat';
import type { SimplePersonne } from '@/types/personneType';
import { computed } from 'vue';

const props = defineProps<{
  personne: SimplePersonne;
}>();

const etat = computed<enumValues>(() => getEtat(props.personne.etat));
</script>

<template>
  <v-list-item :subtitle="personne.uid">
    <template #title>
      {{ personne.cn }}
    </template>
    <template #prepend>
      <v-icon :icon="personne.source.startsWith('SarapisUi_') ? 'far fa-user' : 'fas fa-user'" :color="etat.color" />
    </template>
  </v-list-item>
</template>
