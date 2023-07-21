<script setup lang="ts">
import type { enumValues } from "@/types/enumValuesType";
import { getEtat } from "@/types/enums/Etat";
import type { SearchPersonne } from "@/types/personneType";
import { ref, watch } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const props = defineProps<{
  personne: SearchPersonne;
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
  <v-chip :text="personne.displayName" rounded>
    <template #prepend>
      <v-icon
        :icon="
          personne.source.startsWith('SarapisUi_')
            ? 'far fa-user'
            : 'fas fa-user'
        "
        :color="displayEtat.color"
        :alt="t(displayEtat.i18n)"
        class="mr-2"
      />
    </template>
  </v-chip>
</template>
