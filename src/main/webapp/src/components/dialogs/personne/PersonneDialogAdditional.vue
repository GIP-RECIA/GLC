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
import { setPersonneAdditional } from '@/services/personneService.ts';
import { useConfigurationStore, usePersonneStore } from '@/stores/index.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState.ts';
import type { Filiere } from '@/types/filiereType.ts';
import type { Personne } from '@/types/personneType.ts';
import { toIdentifier } from '@/utils/accountUtils.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import { storeToRefs } from 'pinia';
import { computed, onMounted, ref } from 'vue';
import { toast } from 'vue3-toastify';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { currentStructureId, personneDialogState, attachMode } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { refreshCurrentPersonne } = personneStore;
const { isCurrentPersonne, personneStructure } = storeToRefs(personneStore);

const { t } = useI18n();

const props = defineProps<{
  personne?: Personne;
  filieres?: Array<Filiere>;
}>();

const selected = ref<Array<string>>([]);

const canSave = computed<boolean>(() => {
  return selected.value?.length == personneStructure.value.additionalFonctions?.length
    ? !selected.value.every((entry) => toIdentifier(personneStructure.value.additionalFonctions).includes(entry))
    : true;
});

const saveButton = computed<{ i18n: string; icon: string; color: string }>(() => {
  if (!personneStructure.value.fonctions) {
    if (!personneStructure.value.additionalFonctions)
      return {
        i18n: 'button.attach',
        icon: 'fas fa-link',
        color: 'success',
      };
    if (selected.value.length == 0)
      return {
        i18n: 'button.detach',
        icon: 'fas fa-link-slash',
        color: 'error',
      };
  }
  return {
    i18n: 'button.save',
    icon: 'fas fa-floppy-disk',
    color: 'success',
  };
});

const save = async (): Promise<void> => {
  try {
    const existFunctions = toIdentifier(personneStructure.value.additionalFonctions);
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
  } else personneDialogState.value = PersonneDialogState.Info;
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
  selected.value = toIdentifier(personneStructure.value.additionalFonctions);
});
</script>

<template>
  <v-card-text class="py-0">
    <checkbox-layout
      :filieres="filieres"
      v-model:selected="selected"
      :disabled="toIdentifier(personneStructure.fonctions)"
    />
  </v-card-text>

  <v-card-actions>
    <v-spacer />
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
