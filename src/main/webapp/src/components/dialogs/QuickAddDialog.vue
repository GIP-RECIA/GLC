<script setup lang="ts">
import PersonneSearch from '@/components/search/PersonneSearch.vue';
import { setPersonneAdditionalWithCode, setPersonneAdditionalWithId } from '@/services/personneService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import { toIdentifier } from '@/utils/accountUtils.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import debounce from 'lodash.debounce';
import { storeToRefs } from 'pinia';
import { computed, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useToast } from 'vue-toastification';

const { t } = useI18n();
const toast = useToast();

const configurationStore = useConfigurationStore();
const { isEditAllowed } = configurationStore;
const { currentStructureId, isQuickAdd, requestAdd } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const { currentPersonne, structureFonctions, structureAdditionalFonctions } = storeToRefs(personneStore);

const structureStore = useStructureStore();
const { refreshCurrentStructure } = structureStore;

const modelValue = computed<boolean>({
  get() {
    return isQuickAdd.value;
  },
  set() {},
});

const setSelectedUser = (id: number | undefined) => {
  currentPersonne.value = undefined;
  if (id) initCurrentPersonne(id, false);
  else currentPersonne.value = undefined;
};

const canSave = computed<boolean>(() => {
  const functions: Array<string> = [
    ...new Set(toIdentifier(structureFonctions.value).concat(toIdentifier(structureAdditionalFonctions.value))),
  ];
  const alreadyHasFunction = requestAdd.value?.function && functions.includes(requestAdd.value.function);
  if (alreadyHasFunction) toast.error(t('toast.'));

  return currentPersonne.value ? !alreadyHasFunction : false;
});

const save = async () => {
  if (requestAdd.value?.function) {
    try {
      if (requestAdd.value.type == 'id') {
        await setPersonneAdditionalWithId(
          currentPersonne.value!.id,
          currentStructureId.value!,
          requestAdd.value.function,
        );
      } else {
        await setPersonneAdditionalWithCode(
          currentPersonne.value!.id,
          currentStructureId.value!,
          requestAdd.value.function,
        );
      }
      closeAndResetModal(true);
    } catch (e) {
      errorHandler(e);
      closeAndResetModal(false);
    }
  }
};

const closeAndResetModal = (success?: boolean) => {
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
  <v-dialog v-model="modelValue" scrollable :max-width="1024">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ requestAdd?.i18n && t(requestAdd.i18n) }}</v-toolbar-title>
        <template #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" @click="closeAndResetModal()" />
        </template>
      </v-toolbar>
      <v-card-text class="py-0">
        <personne-search
          :search-list="requestAdd?.searchList"
          @update:select="setSelectedUser"
          :class="currentPersonne && isEditAllowed(currentPersonne.etat) ? 'mb-4' : 'mb-6'"
        />
      </v-card-text>
      <v-card-actions v-if="currentPersonne && isEditAllowed(currentPersonne.etat)">
        <v-spacer />
        <v-btn
          color="success"
          prepend-icon="fas fa-floppy-disk"
          :text="t('button.save')"
          :disabled="!canSave"
          @click="save"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>