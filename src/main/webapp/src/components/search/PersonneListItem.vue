<script setup lang="ts">
import type { enumValues } from "@/types/enumValuesType";
import { getEtat } from "@/types/enums/Etat";
import type { SimplePersonne } from "@/types/personneType";
import { ref, watch } from "vue";

const props = defineProps<{
  personne: SimplePersonne;
}>();

const displayEtat = ref<enumValues>(getEtat(props.personne.etat));

watch(
  () => props.personne.etat,
  (newValue, oldValue) => {
    if (newValue != oldValue) {
      displayEtat.value = getEtat(newValue);
    }
  }
);
</script>

<template>
  <v-list-item :subtitle="personne.uid">
    <template #title>
      {{ personne.cn }}
    </template>
    <template #prepend>
      <v-icon
        :icon="
          personne.source.startsWith('SarapisUi_')
            ? 'far fa-user'
            : 'fas fa-user'
        "
        :color="displayEtat.color"
      />
    </template>
  </v-list-item>
</template>
