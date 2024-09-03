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
import CheckboxLayout from '@/components/layouts/CheckboxLayout.vue';
import { useSaveAttachDetach } from '@/composables';
import { setPersonneAdditional } from '@/services/api';
import { useConfigurationStore, usePersonneStore } from '@/stores';
import type { Filiere, Personne } from '@/types';
import { PersonneDialogState } from '@/types/enums';
import { errorHandler, fonctionsToId } from '@/utils';
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref, watch } from 'vue';
import { toast } from 'vue3-toastify';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { currentStructureId } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { refreshCurrentPersonne } = personneStore;
const { isCurrentPersonne, personneStructure, dialogState, attachMode } = storeToRefs(personneStore);

const { t } = useI18n();

const props = defineProps<{
  personne?: Personne;
  filieres?: Array<Filiere>;
}>();

const selected = ref<Array<string>>([]);

const canSave = computed<boolean>(() => {
  return selected.value?.length == personneStructure.value.additionalFonctions?.length
    ? !selected.value.every((entry) => fonctionsToId(personneStructure.value.additionalFonctions).includes(entry))
    : true;
});

const { isDetach, saveButton } = useSaveAttachDetach();

watch(selected, (newValue) => {
  isDetach.value = newValue.length == 0;
});

const save = async (): Promise<void> => {
  try {
    const existFunctions = fonctionsToId(personneStructure.value.additionalFonctions);
    await setPersonneAdditional(
      props.personne!.id,
      currentStructureId.value!,
      selected.value.filter((checked) => !existFunctions.includes(checked)),
      existFunctions.filter((checked) => !selected.value.includes(checked)),
      saveButton.value.i18n.split('.')[1],
    );
    resetAddMode(true);
  } catch (e) {
    errorHandler(e);
    resetAddMode(false);
  }
};

const cancel = (): void => {
  if (attachMode.value) {
    isCurrentPersonne.value = false;
    attachMode.value = false;
  } else dialogState.value = PersonneDialogState.Info;
};

const resetAddMode = (success?: boolean): void => {
  const title = saveButton.value.i18n.replace('button.', '');
  if (success) {
    refreshCurrentPersonne();
    toast.success(t(`toast.additional.success.${title}`));
  } else if (!success && success != undefined) {
    toast.error(t(`toast.additional.error.${title}`));
  }
  cancel();
};

onMounted(() => {
  selected.value = fonctionsToId(personneStructure.value.additionalFonctions);
});
</script>

<template>
  <v-card-text class="py-0">
    <checkbox-layout
      :filieres="filieres"
      v-model:selected="selected"
      :disabled="fonctionsToId(personneStructure.fonctions)"
    />
  </v-card-text>

  <v-card-actions>
    <v-spacer />
    <v-btn color="secondary" prepend-icon="fas fa-xmark" :text="t('button.cancel')" @click="cancel" />
    <v-btn
      :color="saveButton.color"
      :prepend-icon="saveButton.icon"
      :text="t(saveButton.i18n)"
      :disabled="!canSave"
      @click="save"
    />
  </v-card-actions>
</template>
