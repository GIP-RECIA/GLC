<script setup lang="ts">
import { usePersonneStore } from "@/stores/personneStore";
import type { enumValues } from "@/types/enumValuesType";
import { getEtat } from "@/types/enums/Etat";
import type { SimplePersonne } from "@/types/personneType";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;

const props = defineProps<{
  personne: SimplePersonne;
  variant?: NonNullable<
    "flat" | "text" | "elevated" | "tonal" | "outlined" | "plain"
  >;
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
  <v-card :variant="variant" @click="initCurrentPersonne(personne.id)">
    <v-card-text class="d-flex align-center pa-3">
      <v-icon
        :icon="
          personne.source.startsWith('SarapisUi_')
            ? 'far fa-user'
            : 'fas fa-user'
        "
        :color="displayEtat.color"
        :title="t(displayEtat.i18n)"
        :alt="t(displayEtat.i18n)"
        class="mr-2"
      />{{
        personne.patronyme
          ? `${personne.patronyme} ${personne.givenName}`
          : personne.givenName
      }}
    </v-card-text>
  </v-card>
</template>
