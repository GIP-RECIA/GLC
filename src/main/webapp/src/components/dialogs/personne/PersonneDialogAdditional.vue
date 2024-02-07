<script setup lang="ts">
import CheckboxLayout from '@/components/layouts/CheckboxLayout.vue';
import { setPersonneAdditional } from '@/services/personneService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
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
const { currentStructureId, personneDialogState } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { refreshCurrentPersonne } = personneStore;
const { structureFonctions, hasStructureFonctions, structureAdditionalFonctions, hasStructureAdditionalFonctions } =
  storeToRefs(personneStore);

const { t } = useI18n();

const props = defineProps<{
  personne?: Personne;
  filieres?: Array<Filiere>;
}>();

const selected = ref<Array<string>>([]);

const canSave = computed<boolean>(() => {
  return selected.value?.length == structureAdditionalFonctions.value?.length
    ? !selected.value.every((entry) => toIdentifier(structureAdditionalFonctions.value).includes(entry))
    : true;
});

const saveButton = computed<{ i18n: string; icon: string; color: string }>(() => {
  if (!hasStructureFonctions.value) {
    if (!hasStructureAdditionalFonctions.value)
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

const save = async () => {
  try {
    await setPersonneAdditional(props.personne!.id, currentStructureId.value!, selected.value);
    resetAddMode(true);
  } catch (e) {
    errorHandler(e);
    resetAddMode(false);
  }
};

const cancel = () => {
  personneDialogState.value = PersonneDialogState.Info;
};

const resetAddMode = (success?: boolean) => {
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
  selected.value = toIdentifier(structureAdditionalFonctions.value);
});
</script>

<template>
  <v-card-text class="py-0">
    <checkbox-layout :filieres="filieres" v-model:selected="selected" :disabled="toIdentifier(structureFonctions)" />
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
