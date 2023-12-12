<script setup lang="ts">
import ReadonlyData from '@/components/ReadonlyData.vue';
import CheckboxLayout from '@/components/layouts/CheckboxLayout.vue';
import FonctionsLayout from '@/components/layouts/FonctionsLayout.vue';
import { setPersonneAdditional } from '@/services/personneService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import type { enumValues } from '@/types/enumValuesType.ts';
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
const { isEditAllowed, getLoginOffice } = configurationStore;
const { currentStructureId, structureTab, isAddMode } = storeToRefs(configurationStore);

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

const title = computed<string>(() => {
  if (currentPersonne.value) {
    return isAddMode.value
      ? `${t('person.information.additionalFunction', 2)} - ${currentPersonne.value.cn}`
      : currentPersonne.value.cn;
  }
  return '';
});

const etat = computed<enumValues>(() => {
  return currentPersonne.value
    ? getEtat(currentPersonne.value.etat)
    : {
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

const login = computed<{ i18n: string; info?: string }>(() => {
  if (currentPersonne.value) {
    const office = getLoginOffice(currentPersonne.value.categorie, currentPersonne.value.source);

    return {
      i18n: office ? t('externalLogin') : currentPersonne.value.login,
      info: office ? t(`office.${office}`) : undefined,
    };
  }
  return { i18n: '', info: '' };
});

const suppressDate = computed<string | undefined>(() => {
  return currentPersonne.value?.dateSuppression
    ? format(parseISO(currentPersonne.value.dateSuppression), 'P')
    : undefined;
});

const selected = ref<Array<string>>([]);

const preFill = () => {
  selected.value = toIdentifier(structureAdditionalFonctions.value);
};

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

// Reset modal on close
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

// Pre-fill when user is loaded
watch(currentPersonne, (newValue) => {
  if (isCurrentPersonne.value && newValue && isEditAllowed(newValue.etat)) preFill();
});

// Reset pre-filling on exit add mode
watch(isAddMode, (newValue) => {
  if (!newValue) preFill();
});
</script>

<template>
  <v-dialog v-model="modelValue" scrollable :max-width="1024">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ title }}</v-toolbar-title>
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
      <v-card-text :class="[isAddMode ? 'py-0' : 'pt-0']">
        <div v-if="currentPersonne && !isAddMode">
          <div class="d-flex flex-row flex-wrap">
            <readonly-data v-admin label="uid" :value="currentPersonne.uid" class="modal-flex-item" />
            <readonly-data
              :label="t('person.information.profile')"
              :value="t(getCategoriePersonne(currentPersonne.categorie).i18n)"
              class="modal-flex-item"
            />
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
            <readonly-data :label="t('person.information.login')" class="modal-flex-item">
              <div :title="login.info">{{ login.i18n }}</div>
            </readonly-data>
            <readonly-data :label="t('person.information.status')" class="modal-flex-item">
              <v-tooltip
                :text="t('person.status.deletingDate', { suppressDate })"
                location="bottom start"
                :disabled="suppressDate == undefined"
              >
                <template v-slot:activator="{ props }">
                  <div v-bind="props" class="d-flex flex-row align-center w-fit">
                    <v-icon v-if="etat.color" icon="fas fa-circle" :color="etat.color" size="sm" class="mr-2" />
                    <div>{{ t(etat.i18n) }}</div>
                  </div>
                </template>
              </v-tooltip>
            </readonly-data>
            <readonly-data label="Source" :value="t('source.' + currentPersonne.source)" class="modal-flex-item" />
            <readonly-data
              :label="t('person.information.sourceModificationDate')"
              :value="format(parseISO(currentPersonne.dateSourceModification), 'P')"
              class="modal-flex-item"
            />
          </div>
          <div class="mb-3">
            <b>{{ t('person.information.function', 2) }}</b>
            <fonctions-layout :filieres="filieres" :fonctions="structureFonctions" class="my-0" />
          </div>
          <div v-if="structureTab == Tabs.SchoolStaff" class="mb-3">
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
              class="my-0"
            />
          </div>
        </div>

        <checkbox-layout
          v-if="isAddMode && structureTab == Tabs.SchoolStaff"
          :filieres="customMapping?.filieres"
          v-model:selected="selected"
          :disabled="toIdentifier(structureFonctions)"
        />
      </v-card-text>
      <v-card-actions v-if="isAddMode">
        <v-spacer />
        <div v-if="structureTab == Tabs.SchoolStaff">
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
