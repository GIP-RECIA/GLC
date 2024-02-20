<script setup lang="ts">
import PersonneDialogAdditional from '@/components/dialogs/personne/PersonneDialogAdditional.vue';
import PersonneDialogInfo from '@/components/dialogs/personne/PersonneDialogInfo.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState';
import { Tabs } from '@/types/enums/Tabs.ts';
import debounce from 'lodash.debounce';
import { storeToRefs } from 'pinia';
import { computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { isEditAllowed } = configurationStore;
const { structureTab, personneDialogState } = storeToRefs(configurationStore);

const stcuctureStore = useStructureStore();
const { fonction } = storeToRefs(stcuctureStore);

const personneStore = usePersonneStore();
const { currentPersonne, isCurrentPersonne } = storeToRefs(personneStore);

const { t } = useI18n();

const modelValue = computed<boolean>({
  get() {
    return isCurrentPersonne.value;
  },
  set() {},
});

const title = computed<string>(() => {
  if (!currentPersonne.value) return '';
  const { cn } = currentPersonne.value;
  switch (personneDialogState.value) {
    case PersonneDialogState.ManageAdditional:
      return `${t('person.information.additionalFunction', 2)} - ${cn}`;
    case PersonneDialogState.Info:
    default:
      return cn;
  }
});

// Reset modal on close
watch(isCurrentPersonne, (newValue) => {
  if (!newValue) {
    const reset = debounce(() => {
      currentPersonne.value = undefined;
      personneDialogState.value = PersonneDialogState.Info;
    }, 200);
    reset();
  }
});
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ title }}</v-toolbar-title>
        <template v-if="personneDialogState == PersonneDialogState.Info" #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" @click="isCurrentPersonne = false" />
        </template>
      </v-toolbar>

      <personne-dialog-info v-if="personneDialogState == PersonneDialogState.Info" :personne="currentPersonne" />

      <personne-dialog-additional
        v-if="
          personneDialogState == PersonneDialogState.ManageAdditional &&
          structureTab == Tabs.SchoolStaff &&
          fonction?.customMapping &&
          isEditAllowed(currentPersonne ? currentPersonne.etat : '')
        "
        :personne="currentPersonne"
        :filieres="fonction?.customMapping?.filieres"
      />
    </v-card>
  </v-dialog>
</template>
