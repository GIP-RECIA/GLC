<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import PersonneSearch from '@/components/search/personne/PersonneSearch.vue';
import { setPersonneAdditionalWithCode, setPersonneAdditionalWithId } from '@/services/personneService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { toIdentifier } from '@/utils/accountUtils.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import debounce from 'lodash.debounce';
import { storeToRefs } from 'pinia';
import { computed, ref, watch } from 'vue';
import { toast } from 'vue3-toastify';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { isEditAllowed } = configurationStore;
const { currentStructureId, isQuickAdd, requestAdd } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const {
  currentPersonne,
  structureFonctions,
  hasStructureFonctions,
  structureAdditionalFonctions,
  hasStructureAdditionalFonctions,
} = storeToRefs(personneStore);

const structureStore = useStructureStore();
const { refreshCurrentStructure } = structureStore;

const { t } = useI18n();

const modelValue = computed<boolean>({
  get() {
    return isQuickAdd.value;
  },
  set() {},
});

const user = ref<SimplePersonne | undefined>();
const selectedUser = computed<SimplePersonne | undefined>({
  get() {
    return user.value;
  },
  set(selected) {
    currentPersonne.value = undefined;
    if (selected) {
      user.value = selected;
      initCurrentPersonne(selected.id, false);
    } else user.value = undefined;
  },
});

const canSave = computed<boolean>(() => {
  const functions: Array<string> = [
    ...new Set(toIdentifier(structureFonctions.value).concat(toIdentifier(structureAdditionalFonctions.value))),
  ];
  const alreadyHasFunction = requestAdd.value?.function && functions.includes(requestAdd.value.function);
  if (alreadyHasFunction) toast.error(t('toast.'));

  return currentPersonne.value ? !alreadyHasFunction : false;
});

const saveButton = computed<{ i18n: string; icon: string; color: string }>(() => {
  if (!hasStructureFonctions.value && !hasStructureAdditionalFonctions.value)
    return {
      i18n: 'button.attach',
      icon: 'fas fa-link',
      color: 'success',
    };
  return {
    i18n: 'button.save',
    icon: 'fas fa-floppy-disk',
    color: 'success',
  };
});

const save = async (): Promise<void> => {
  if (requestAdd.value?.function) {
    try {
      if (requestAdd.value.type == 'id') {
        await setPersonneAdditionalWithId(
          currentPersonne.value!.id,
          currentStructureId.value!,
          requestAdd.value.function,
          saveButton.value.i18n.split('.')[1],
        );
      } else {
        await setPersonneAdditionalWithCode(
          currentPersonne.value!.id,
          currentStructureId.value!,
          requestAdd.value.function,
          saveButton.value.i18n.split('.')[1],
        );
      }
      closeAndResetModal(true);
    } catch (e) {
      errorHandler(e);
      closeAndResetModal(false);
    }
  }
};

const closeAndResetModal = (success?: boolean): void => {
  if (success) {
    refreshCurrentStructure();
    toast.success(t('toast.additional.success.save'));
  } else if (!success && success != undefined) {
    toast.error(t('toast.additional.error.save'));
  }

  if (isQuickAdd.value) isQuickAdd.value = false;
  const reset = debounce(() => {
    currentPersonne.value = undefined;
  }, 200);
  reset();
};

watch(currentPersonne, (newValue) => {
  if (isQuickAdd.value && newValue) {
    if (!isEditAllowed(newValue.etat)) toast.error(t('toast.editStatusDenied'));
  }
});
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024 / 2">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ requestAdd?.i18n && t(requestAdd.i18n) }}</v-toolbar-title>
        <template #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" @click="closeAndResetModal()" />
        </template>
      </v-toolbar>
      <v-card-text class="py-0">
        <personne-search
          v-model="selectedUser"
          :search-list="requestAdd?.searchList"
          variant="outlined"
          :class="currentPersonne && isEditAllowed(currentPersonne.etat) ? 'mb-4' : 'mb-6'"
        />
      </v-card-text>
      <v-card-actions v-if="currentPersonne && isEditAllowed(currentPersonne.etat)">
        <v-spacer />
        <v-btn
          :color="saveButton.color"
          :prepend-icon="saveButton.icon"
          :text="t(saveButton.i18n)"
          :disabled="!canSave"
          @click="save"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
