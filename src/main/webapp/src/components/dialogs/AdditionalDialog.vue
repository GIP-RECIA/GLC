<script setup lang="ts">
import CheckboxLayout from '@/components/layouts/CheckboxLayout.vue';
import PersonneSearch from '@/components/search/personne/PersonneSearch.vue';
import { setPersonneAdditional } from '@/services/personneService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
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
const { currentStructureId, structureTab, isAdditional } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { customMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne } = personneStore;
const {
  currentPersonne,
  structureFonctions,
  hasStructureFonctions,
  structureAdditionalFonctions,
  hasStructureAdditionalFonctions,
  administrativeList,
} = storeToRefs(personneStore);

const structureStore = useStructureStore();
const { refreshCurrentStructure } = structureStore;

const { t } = useI18n();

const modelValue = computed<boolean>({
  get() {
    return isAdditional.value;
  },
  set() {},
});

const setSelectedUser = (id: number | undefined) => {
  currentPersonne.value = undefined;
  if (id) {
    initCurrentPersonne(id, false);
  } else currentPersonne.value = undefined;
};

const selected = ref<Array<string>>([]);

const preFill = () => {
  selected.value = toIdentifier(structureAdditionalFonctions.value);
};

const canSave = computed<boolean>(() => {
  return selected.value.length == structureAdditionalFonctions.value?.length
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
    await setPersonneAdditional(currentPersonne.value!.id, currentStructureId.value!, selected.value);
    closeAndResetModal(true);
  } catch (e) {
    errorHandler(e);
    closeAndResetModal(false);
  }
};

const closeAndResetModal = (success?: boolean) => {
  const { i18n } = saveButton.value;
  const title = i18n.replace('button.', '');
  if (success) {
    refreshCurrentStructure();
    toast.success(t(`toast.additional.success.${title}`));
  } else if (!success && success != undefined) {
    toast.error(t(`toast.additional.error.${title}`));
  }

  if (isAdditional.value) isAdditional.value = false;
  const reset = debounce(() => {
    currentPersonne.value = undefined;
    selected.value = [];
  }, 200);
  reset();
};

const currentTabValue = computed<{
  title: string;
  searchList: Array<SimplePersonne> | undefined;
  filieres: Array<any> | undefined;
}>(() => {
  switch (structureTab.value) {
    case Tabs.SchoolStaff:
      return {
        title: t('additional.fonction.add'),
        searchList: administrativeList.value,
        filieres: customMapping.value?.filieres,
      };
    default:
      return { title: '', searchList: undefined, filieres: undefined };
  }
});

// Pre-fill when user is loaded
watch(currentPersonne, (newValue) => {
  if (isAdditional.value && newValue) {
    if (isEditAllowed(newValue.etat)) preFill();
    else toast.error(t('toast.editStatusDenied'));
  }
});

// Reset pre-filling on exit
watch(isAdditional, (newValue) => {
  if (!newValue) preFill();
});
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ currentTabValue.title }}</v-toolbar-title>
        <template #append>
          <v-btn icon="fas fa-xmark" color="default" variant="plain" @click="closeAndResetModal()" />
        </template>
      </v-toolbar>
      <v-card-text class="py-0">
        <personne-search
          :search-list="currentTabValue.searchList"
          @update:select="setSelectedUser"
          :class="currentPersonne && isEditAllowed(currentPersonne.etat) ? 'mb-4' : 'mb-6'"
        />
        <checkbox-layout
          v-if="currentPersonne && isEditAllowed(currentPersonne.etat)"
          :filieres="currentTabValue.filieres"
          v-model:selected="selected"
          :disabled="toIdentifier(structureFonctions)"
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
