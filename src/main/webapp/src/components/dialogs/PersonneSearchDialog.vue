<script setup lang="ts">
import PersonneSearchOut from '@/components/search/personne/PersonneSearchOut.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState';
import type { SimplePersonne } from '@/types/personneType.ts';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { personneDialogState, isPersonneSearch } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const { currentPersonne } = storeToRefs(personneStore);

const { t } = useI18n();

const modelValue = computed<boolean>({
  get() {
    return isPersonneSearch.value;
  },
  set() {},
});

const selectedUser = computed<SimplePersonne | undefined>({
  get() {
    return undefined;
  },
  set(user) {
    currentPersonne.value = undefined;
    if (user) {
      isPersonneSearch.value = false;
      personneDialogState.value = PersonneDialogState.ManageAdditional;
      initCurrentPersonne(user.id, true);
    }
  },
});
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024 / 2">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ t('button.attach') }}</v-toolbar-title>
        <template #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" @click="isPersonneSearch = false" />
        </template>
      </v-toolbar>
      <v-card-text class="pt-0 pb-6">
        <personne-search-out v-model="selectedUser" />
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
