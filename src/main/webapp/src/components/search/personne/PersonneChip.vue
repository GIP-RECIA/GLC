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
  <v-chip :text="concatenate([personne.cn, personne.email, personne.uid], ' - ')" rounded>
    <template #prepend>
      <v-icon :icon="getIcon(personne.source)" :color="etat.color" class="mr-2" />
    </template>
  </v-chip>
</template>
