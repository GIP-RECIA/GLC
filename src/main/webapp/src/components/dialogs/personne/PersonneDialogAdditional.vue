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
import CheckboxForm from '@/components/forms/CheckboxForm.vue';
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

const data = ref<{
  baseSelection: Array<string>;
  selected: Array<string>;
  disabled: Array<string>;
}>({
  baseSelection: [],
  selected: [],
  disabled: [],
});

const canSave = computed<boolean>(() => {
  const { additionalFonctions } = personneStructure.value;
  const { baseSelection, selected } = data.value;

  return selected.length == additionalFonctions?.length
    ? !selected.every((entry) => baseSelection.includes(entry))
    : true;
});

const { isDetach, saveButton } = useSaveAttachDetach();

watch(
  () => data.value.selected,
  (newValue) => {
    isDetach.value = newValue.length == 0;
  },
);

const save = async (): Promise<void> => {
  try {
    const { baseSelection, selected } = data.value;
    await setPersonneAdditional(
      props.personne!.id,
      currentStructureId.value!,
      selected.filter((checked) => !baseSelection.includes(checked)),
      baseSelection.filter((checked) => !selected.includes(checked)),
      saveButton.value.i18n.split('.')[1],
    );
    resetAddMode(true);
  } catch (e) {
    errorHandler(e);
    resetAddMode(false);
  }
};

const cancel = (): void => {
  if (attachMode.value) isCurrentPersonne.value = false;
  else dialogState.value = PersonneDialogState.Info;
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
  const { fonctions, additionalFonctions } = personneStructure.value;
  const selected = fonctionsToId(additionalFonctions);
  data.value = {
    baseSelection: selected,
    selected,
    disabled: fonctionsToId(fonctions),
  };
});
</script>

<template>
  <v-card-text class="py-0">
    <checkbox-form v-model="data.selected" :filieres="filieres" :disabled="data.disabled" />
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
