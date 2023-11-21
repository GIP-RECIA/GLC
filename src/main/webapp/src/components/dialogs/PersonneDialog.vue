<script setup lang="ts">
import ReadonlyData from '@/components/ReadonlyData.vue';
import CheckboxLayout from '@/components/layouts/CheckboxLayout.vue';
import FonctionsLayout from '@/components/layouts/FonctionsLayout.vue';
import { setPersonneAdditional } from '@/services/personneService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import type { enumValues } from '@/types/enumValuesType.ts';
import { Etat } from '@/types/enums/Etat.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import { getCategoriePersonne, getEtat, toIdentifier } from '@/utils/accountUtils.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import { format, getYear, parseISO } from 'date-fns';
import debounce from 'lodash.debounce';
import { storeToRefs } from 'pinia';
import { computed, ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';
import { useToast } from 'vue-toastification';

const { t } = useI18n();
const toast = useToast();

const configurationStore = useConfigurationStore();
const { isExternalLogin, isEditAllowed } = configurationStore;
const { currentTab, currentStructureId, isAddMode } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { filieres, customMapping, isCustomMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { refreshCurrentPersonne } = personneStore;
const {
  currentPersonne,
  isCurrentPersonne,
  structureFonctions,
  hasStructureFonctions,
  structureAdditionalFonctions,
  hasStructureAdditionalFonctions,
} = storeToRefs(personneStore);

const modelValue = computed<boolean>({
  get() {
    return isCurrentPersonne.value;
  },
  set() {},
});

watch(isCurrentPersonne, (newValue) => {
  if (!newValue) {
    const reset = debounce(() => {
      currentPersonne.value = undefined;
      isAddMode.value = false;
    }, 200);
    reset();
    selected.value = [];
  }
});

watch(currentPersonne, (newValue) => {
  if (isCurrentPersonne.value && newValue) {
    if (isEditAllowed(newValue.etat)) selected.value = toIdentifier(structureAdditionalFonctions.value);

    isLocked.value = currentPersonne.value!.etat == Etat.Bloque;
  }
});

watch(isAddMode, (newValue) => {
  if (!newValue) {
    selected.value = toIdentifier(structureAdditionalFonctions.value);
  }
});

const etat = computed<enumValues>(() => {
  if (currentPersonne.value) return getEtat(currentPersonne.value.etat);
  return {
    i18n: '',
    color: '',
  };
});

const schoolYear = computed<string | undefined>(() => {
  if (currentPersonne.value?.anneeScolaire) {
    const year = getYear(parseISO(currentPersonne.value.anneeScolaire));
    return `${year}/${year + 1}`;
  }
  return undefined;
});

const selected = ref<Array<string>>([]);

const setSelected = (value: Array<string>) => {
  selected.value = value;
};

const canSave = computed<boolean>(() => {
  if (selected.value?.length == structureAdditionalFonctions.value?.length) {
    return !selected.value.every((entry) => toIdentifier(structureAdditionalFonctions.value).includes(entry));
  }
  return true;
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
    resetAddMode(true);
  } catch (e) {
    errorHandler(e);
    resetAddMode(false);
  }
};

const cancel = () => {
  isAddMode.value = false;
};

const reinitialize = () => {};

const isLocked = ref<boolean>(false);

const lockManager = () => {
  isLocked.value = !isLocked.value;
};

const resetAddMode = (success?: boolean) => {
  const { i18n } = saveButton.value;
  const title = i18n.replace('button.', '');
  if (success) {
    refreshCurrentPersonne();
    toast.success(t(`toast.additional.success.${title}`));
  } else if (!success && success != undefined) {
    toast.error(t(`toast.additional.error.${title}`));
  }
  isAddMode.value = false;
};
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ currentPersonne ? currentPersonne.cn : '' }}</v-toolbar-title>
        <template #append>
          <v-btn
            v-if="!isAddMode"
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            @click="isCurrentPersonne = false"
          />
        </template>
      </v-toolbar>
      <v-card-text class="py-0">
        <div v-if="currentPersonne && !isAddMode">
          <div class="d-flex flex-row flex-wrap">
            <readonly-data v-admin label="uid" :value="currentPersonne.uid" class="modal-flex-item" />
            <readonly-data
              :label="t('person.information.civility')"
              :value="currentPersonne.civilite"
              class="modal-flex-item"
            />
            <readonly-data
              :label="t('person.information.lastName')"
              :value="currentPersonne.patronyme"
              class="modal-flex-item"
            />
            <readonly-data
              :label="t('person.information.firstName')"
              :value="currentPersonne.givenName"
              class="modal-flex-item"
            />
            <readonly-data
              :label="t('person.information.birthDate')"
              :value="currentPersonne.dateNaissance ? format(parseISO(currentPersonne.dateNaissance), 'P') : undefined"
              class="modal-flex-item"
            />
            <readonly-data
              :label="t('person.information.email')"
              :value="currentPersonne.email"
              class="modal-flex-item"
            />
            <readonly-data :label="t('person.information.schoolYear')" :value="schoolYear" class="modal-flex-item" />
            <readonly-data
              :label="t('person.information.login')"
              :value="
                isExternalLogin(currentPersonne.categorie, currentPersonne.source)
                  ? t('externalLogin')
                  : currentPersonne.login
              "
              class="modal-flex-item"
            />
            <readonly-data :label="t('person.information.status')" class="modal-flex-item">
              <div class="d-flex flex-row align-center">
                <v-icon v-if="etat.color" icon="fas fa-circle" :color="etat.color" size="sm" class="mr-2" />
                <div>{{ t(etat.i18n) }}</div>
              </div>
            </readonly-data>
            <readonly-data
              :label="t('person.information.profile')"
              :value="t(getCategoriePersonne(currentPersonne.categorie).i18n)"
              class="modal-flex-item"
            />
            <readonly-data label="Source" :value="t('source.' + currentPersonne.source)" class="modal-flex-item" />
          </div>
          <div class="mb-4">
            <b>{{ t('person.information.function', 2) }}</b>
            <fonctions-layout :filieres="filieres" :fonctions="structureFonctions" class="mt-2" />
          </div>
          <div v-if="currentTab == Tabs.AdministrativeStaff" class="mb-4">
            <div class="d-flex align-center">
              <b>{{ t('person.information.additionalFunction', 2) }}</b>
              <v-btn
                v-if="isCustomMapping && isEditAllowed(currentPersonne.etat)"
                color="primary"
                variant="tonal"
                density="compact"
                :text="t(hasStructureAdditionalFonctions ? 'button.edit' : 'button.add')"
                class="ml-2"
                @click="isAddMode = true"
              >
                <template #prepend>
                  <v-icon :icon="hasStructureAdditionalFonctions ? 'fas fa-pen' : 'fas fa-plus'" size="sm" />
                </template>
              </v-btn>
            </div>
            <fonctions-layout
              :filieres="customMapping?.filieres"
              :fonctions="structureAdditionalFonctions"
              class="mt-2"
            />
          </div>
          <div v-if="currentTab == Tabs.TeachingStaff" class="mb-4">
            <b>{{ t('person.information.additionalTeaching', 2) }}</b>
            <fonctions-layout :filieres="undefined" :fonctions="structureAdditionalFonctions" class="mt-2" />
          </div>
        </div>

        <checkbox-layout
          v-if="isAddMode && currentTab == Tabs.AdministrativeStaff"
          :filieres="customMapping?.filieres"
          :selected="selected"
          :disabled="toIdentifier(structureFonctions)"
          @update:selected="setSelected"
        />
      </v-card-text>
      <v-card-actions>
        <div v-if="!isAddMode">
          <v-btn
            v-if="currentPersonne?.etat == Etat.Bloque || currentPersonne?.etat == Etat.Valide"
            color="secondary"
            :prepend-icon="isLocked ? 'fas fa-lock-open' : 'fas fa-lock'"
            :text="isLocked ? t('button.unlock') : t('button.lock')"
            @click="lockManager"
          />
          <v-btn
            color="secondary"
            prepend-icon="fas fa-rotate-right"
            :text="t('button.reinitialize')"
            @click="reinitialize"
          />
        </div>
        <v-spacer />
        <div v-if="isAddMode && currentTab == Tabs.AdministrativeStaff">
          <v-btn color="secondary" prepend-icon="fas fa-xmark" :text="t('button.cancel')" @click="cancel" />
          <v-btn
            :color="saveButton.color"
            :prepend-icon="saveButton.icon"
            :text="t(saveButton.i18n)"
            :disabled="!canSave"
            @click="save"
          />
        </div>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
