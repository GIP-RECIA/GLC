<script setup lang="ts">
import type { enumValues } from '@/types/enumValuesType';
import type { SimplePersonne } from '@/types/personneType';
import { getEtat, getIcon } from '@/utils/accountUtils';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  personne: SimplePersonne;
}>();

const etat = computed<enumValues>(() => getEtat(props.personne.etat));
const text = computed<string>(() => {
  let str = props.personne.cn;
  if (props.personne.email) str += ' - ' + props.personne.email;
  if (str.length != props.personne.cn.length) str += ' - ';
  if (props.personne.uid) str += props.personne.uid;

  return str;
});
</script>

<template>
  <v-chip :text="text" rounded>
    <template #prepend>
      <v-icon :icon="getIcon(personne.source)" :color="etat.color" :alt="t(etat.i18n)" class="mr-2" />
    </template>
  </v-chip>
</template>
