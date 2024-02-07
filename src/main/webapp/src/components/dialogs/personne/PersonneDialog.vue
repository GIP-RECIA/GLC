<script setup lang="ts">
import PersonneDialogAdditional from '@/components/dialogs/personne/PersonneDialogAdditional.vue';
import PersonneDialogInfo from '@/components/dialogs/personne/PersonneDialogInfo.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import debounce from 'lodash.debounce';
import { storeToRefs } from 'pinia';
import { computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { isEditAllowed } = configurationStore;
const { structureTab, isAddMode } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { customMapping, isCustomMapping } = storeToRefs(fonctionStore);

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
  if (currentPersonne.value) {
    return isAddMode.value
      ? `${t('person.information.additionalFunction', 2)} - ${currentPersonne.value.cn}`
      : currentPersonne.value.cn;
  }
  return '';
});

// Reset modal on close
watch(isCurrentPersonne, (newValue) => {
  if (!newValue) {
    const reset = debounce(() => {
      currentPersonne.value = undefined;
      isAddMode.value = false;
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
        <template v-if="!isAddMode" #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" @click="isCurrentPersonne = false" />
        </template>
      </v-toolbar>

      <personne-dialog-info v-if="!isAddMode" :personne="currentPersonne" />

      <personne-dialog-additional
        v-if="
          isAddMode &&
          structureTab == Tabs.SchoolStaff &&
          isCustomMapping &&
          isEditAllowed(currentPersonne ? currentPersonne.etat : '')
        "
        :personne="currentPersonne"
        :filieres="customMapping?.filieres"
      />
    </v-card>
  </v-dialog>
</template>
