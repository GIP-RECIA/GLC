<script setup lang="ts">
import { usePersonneStore } from "@/stores/personneStore";
import type { enumValues } from "@/types/enumValuesType";
import { getEtat } from "@/types/enums/Etat";
import type { SimplePersonne } from "@/types/personneType";
import { computed } from "vue";
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

const etat = computed<enumValues>(() => getEtat(props.personne.etat));
</script>

<template>
  <v-card :variant="variant" @click="initCurrentPersonne(personne.id, true)">
    <v-card-text class="d-flex align-center pa-3">
      <v-icon
        :icon="
          personne.source.startsWith('SarapisUi_')
            ? 'far fa-user'
            : 'fas fa-user'
        "
        :color="etat.color"
        :title="t(etat.i18n)"
        :alt="t(etat.i18n)"
        class="mr-2"
      />{{ personne.cn }}
    </v-card-text>
  </v-card>
</template>
