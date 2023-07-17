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
  user: SimplePersonne;
  variant?: NonNullable<
    "flat" | "text" | "elevated" | "tonal" | "outlined" | "plain"
  >;
}>();

const displayEtat = ref<enumValues>(getEtat(props.user.etat));

watch(
  () => props.user.etat,
  (newValue, oldValue) => {
    if (newValue != oldValue) {
      displayEtat.value = getEtat(newValue);
    }
  }
);
</script>

<template>
  <v-card :variant="variant" @click="initCurrentPersonne(user.id)">
    <v-card-text class="d-flex align-center pa-3">
      <v-icon
        :icon="
          user.source.startsWith('SarapisUi_') ? 'far fa-user' : 'fas fa-user'
        "
        :color="displayEtat.color"
        :title="t(displayEtat.i18n)"
        :alt="t(displayEtat.i18n)"
        class="mr-2"
      />{{
        user.patronyme ? `${user.patronyme} ${user.givenName}` : user.givenName
      }}
    </v-card-text>
  </v-card>
</template>
