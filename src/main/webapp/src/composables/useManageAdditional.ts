/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { useSaveAttachDetach } from './useSaveAttachDetach';
import { addPersonneAdditionalV2, deletePersonneAdditionalV2, setPersonneAdditional } from '@/services/api';
import { useConfigurationStore, usePersonneStore } from '@/stores';
import type { FonctionForm } from '@/types';
import { PersonneDialogState } from '@/types/enums';
import { errorHandler, filiereDisciplineToId, fonctionToId, fonctionsToId } from '@/utils';
import { storeToRefs } from 'pinia';
import { computed, onBeforeMount, ref, watch } from 'vue';
import { toast } from 'vue3-toastify';
import { useI18n } from 'vue-i18n';

const useManageAdditional = () => {
  const configurationStore = useConfigurationStore();
  const { currentStructureId } = storeToRefs(configurationStore);

  const personneStore = usePersonneStore();
  const { refreshCurrentPersonne } = personneStore;
  const { currentPersonne, isCurrentPersonne, personneStructure, dialogState, editFunction, attachMode } =
    storeToRefs(personneStore);

  const { t } = useI18n();

  const data = ref<{
    baseSelection: Array<FonctionForm>;
    selected: Array<FonctionForm>;
    disabled: Array<string>;
  }>({
    baseSelection: [],
    selected: [],
    disabled: [],
  });

  const canSave = computed<boolean>(() => {
    const { baseSelection, selected } = data.value;
    switch (dialogState.value) {
      case PersonneDialogState.ManageAdditional:
        if (selected[0]) {
          return baseSelection[0]
            ? !!selected[0].date && selected[0].date != baseSelection[0].date
            : !!selected[0].fonction && !!selected[0].date;
        }
        break;
      case PersonneDialogState.ManageAdditionalMultiple:
        return selected.length == baseSelection.length
          ? !selected.every((entry) => baseSelection.includes(entry))
          : true;
    }
    return false;
  });

  const { isDetach, saveButton } = useSaveAttachDetach();

  watch(
    () => data.value.selected,
    (newValue) => {
      isDetach.value = dialogState.value == PersonneDialogState.ManageAdditionalMultiple ? newValue.length == 0 : false;
    },
  );

  const canDelete = computed<boolean>(() => {
    return !attachMode.value && !!data.value.baseSelection[0];
  });

  const onSave = async (): Promise<void> => {
    if (!isCurrentPersonne.value) return;
    try {
      const personneId = currentPersonne.value!.id;
      const structureId = currentStructureId.value!;
      const requiredAction = saveButton.value.i18n.split('.')[1];
      // TODO: Change API => Use existing API witch not support date
      const { baseSelection, selected } = data.value;
      const selectedF: Array<string> = selected.map(({ fonction }) => fonction).filter((entry) => entry != undefined);
      const baseF: Array<string> =
        baseSelection.length > 0
          ? baseSelection.map(({ fonction }) => fonction).filter((entry) => entry != undefined)
          : [];
      switch (dialogState.value) {
        case PersonneDialogState.ManageAdditional:
          await addPersonneAdditionalV2(personneId, structureId, selected[0]);
          break;
        case PersonneDialogState.ManageAdditionalMultiple:
          await setPersonneAdditional(
            personneId,
            structureId,
            selectedF.filter((checked) => !baseF.includes(checked)),
            baseF.filter((checked) => !selectedF.includes(checked)),
            requiredAction,
          );
          break;
      }
      resetAddMode(true);
    } catch (e) {
      errorHandler(e);
      resetAddMode(false);
    }
  };

  const onCancel = (): void => {
    if (attachMode.value) isCurrentPersonne.value = false;
    else {
      if (dialogState.value == PersonneDialogState.ManageAdditional) editFunction.value = undefined;
      dialogState.value = PersonneDialogState.Info;
    }
  };

  const onDelete = async (): Promise<void> => {
    if (!isCurrentPersonne.value) return;
    try {
      const personneId = currentPersonne.value!.id;
      const structureId = currentStructureId.value!;
      const { baseSelection } = data.value;
      await deletePersonneAdditionalV2(personneId, structureId, baseSelection[0].fonction);
      resetAddMode(true);
    } catch (e) {
      errorHandler(e);
      resetAddMode(false);
    }
  };

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
    const { fonctions, additionalFonctions } = personneStructure.value;
    let selected: Array<FonctionForm> = [];
    let disabled: Array<string> = fonctionsToId(fonctions);
    switch (dialogState.value) {
      case PersonneDialogState.ManageAdditional:
        if (editFunction.value) {
          const { filiere, discipline, dateFin } = editFunction.value;
          selected.push({
            fonction: filiereDisciplineToId(filiere, discipline),
            date: dateFin,
          });
          disabled = disabled
            .concat(fonctionsToId(additionalFonctions))
            .filter((fonction) => fonction != selected[0].fonction);
        } else {
          disabled = disabled.concat(fonctionsToId(additionalFonctions));
        }
        break;
      case PersonneDialogState.ManageAdditionalMultiple:
        selected =
          additionalFonctions?.map((fonction) => {
            return { fonction: fonctionToId(fonction), date: fonction.dateFin };
          }) ?? [];
        break;
    }
    data.value = {
      baseSelection: [...selected],
      selected,
      disabled,
    };
  });

  return { data, canSave, saveButton, canDelete, onSave, onCancel, onDelete };
};

export { useManageAdditional };
