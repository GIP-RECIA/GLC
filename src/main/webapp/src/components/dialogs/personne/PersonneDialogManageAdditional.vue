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
import FonctionForm from '@/components/forms/FonctionForm.vue';
import { useSaveAttachDetach } from '@/composables';
import { setPersonneAdditional } from '@/services/api';
import { useConfigurationStore, usePersonneStore } from '@/stores';
import type { Filiere, Personne } from '@/types';
import { PersonneDialogState } from '@/types/enums';
import { errorHandler, filiereDisciplineToId, fonctionsToId } from '@/utils';
import { storeToRefs } from 'pinia';
import { computed, onBeforeMount, ref } from 'vue';
import { toast } from 'vue3-toastify';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { currentStructureId } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { refreshCurrentPersonne } = personneStore;
const { isCurrentPersonne, personneStructure, dialogState, editFunction, attachMode } = storeToRefs(personneStore);

const { t } = useI18n();

const props = defineProps<{
  personne?: Personne;
  filieres?: Array<Filiere>;
}>();

const data = ref<{
  baseSelection:
    | {
        fonction: string;
        date: string;
      }
    | undefined;
  selected:
    | {
        fonction: string;
        date: string;
      }
    | undefined;
  disabled: Array<string>;
}>({
  baseSelection: undefined,
  selected: undefined,
  disabled: [],
});

const canSave = computed<boolean>(() => {
  if (!data.value.selected) return false;
  const { fonction, date } = data.value.selected;

  return fonction != '' && date != '';
});

const canDelete = false;

const { saveButton } = useSaveAttachDetach();

const onSave = async (): Promise<void> => {
  try {
    const { baseSelection, selected } = data.value;
    // await setPersonneAdditional(
    //   props.personne!.id,
    //   currentStructureId.value!,
    //   selected.filter((checked) => !baseSelection.includes(checked)),
    //   baseSelection.filter((checked) => !selected.includes(checked)),
    //   saveButton.value.i18n.split('.')[1],
    // );
    resetAddMode(true);
  } catch (e) {
    errorHandler(e);
    resetAddMode(false);
  }
};

const onCancel = (): void => {
  if (attachMode.value) isCurrentPersonne.value = false;
  else {
    editFunction.value = undefined;
    dialogState.value = PersonneDialogState.Info;
  }
};

const onDelete = (): void => {};

const resetAddMode = (success?: boolean): void => {
  const title = saveButton.value.i18n.replace('button.', '');
  if (success) {
    refreshCurrentPersonne();
    toast.success(t(`toast.additional.success.${title}`));
  } else if (!success && success != undefined) {
    toast.error(t(`toast.additional.error.${title}`));
  }
  onCancel();
};

onBeforeMount(() => {
  const { fonctions } = personneStructure.value;
  let selected = undefined;

  if (editFunction.value) {
    const { filiere, discipline, dateFin } = editFunction.value;
    selected = {
      fonction: filiereDisciplineToId(filiere, discipline),
      date: dateFin ?? '',
    };
  }
  data.value = {
    baseSelection: selected,
    selected,
    disabled: fonctionsToId(fonctions),
  };
});
</script>

<template>
  <v-card-text class="pt-0 py-3">
    <fonction-form v-model="data.selected" :filieres="filieres" :disabled="data.disabled" />
  </v-card-text>

  <v-card-actions>
    <v-btn
      color="error"
      prepend-icon="fas fa-trash"
      :text="t('button.delete')"
      :disabled="!canDelete"
      @click="onDelete"
    />
    <v-spacer />
    <v-btn color="secondary" prepend-icon="fas fa-xmark" :text="t('button.cancel')" @click="onCancel" />
    <v-btn
      :color="saveButton.color"
      :prepend-icon="saveButton.icon"
      :text="t(saveButton.i18n)"
      :disabled="!canSave"
      @click="onSave"
    />
  </v-card-actions>
</template>
