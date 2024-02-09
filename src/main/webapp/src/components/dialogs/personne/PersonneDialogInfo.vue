<script setup lang="ts">
import ReadonlyData from '@/components/ReadonlyData.vue';
import FonctionsLayout from '@/components/layouts/FonctionsLayout.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useFonctionStore } from '@/stores/fonctionStore.ts';
import { usePersonneStore } from '@/stores/personneStore.ts';
import type { enumValues } from '@/types/enumValuesType.ts';
import { CategoriePersonne } from '@/types/enums/CategoriePersonne.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import type { Personne } from '@/types/personneType.ts';
import { getCategoriePersonne, getEtat, getIcon } from '@/utils/accountUtils.ts';
import { useSessionStorage } from '@vueuse/core';
import { format, getYear, parseISO } from 'date-fns';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { isEditAllowed, getLoginOffice } = configurationStore;
const { structureTab, personneDialogState } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { allFilieres, isCustomMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { structureFonctions, hasStructureFonctions, structureAdditionalFonctions, hasStructureAdditionalFonctions } =
  storeToRefs(personneStore);

const { t } = useI18n();

const props = defineProps<{
  personne?: Personne;
}>();

const etat = computed<enumValues>(() => {
  return props.personne
    ? getEtat(props.personne.etat)
    : {
        i18n: '',
        color: '',
      };
});

const schoolYear = computed<string | undefined>(() => {
  if (props.personne?.anneeScolaire) {
    const year = getYear(parseISO(props.personne.anneeScolaire));
    return `${year}/${year + 1}`;
  }
  return undefined;
});

const login = computed<{ i18n: string; info?: string }>(() => {
  if (!props.personne) return { i18n: '', info: '' };
  const office = getLoginOffice(props.personne.categorie, props.personne.source);

  return {
    i18n: office ? t('externalLogin') : props.personne.login,
    info: office ? t(`office.${office}`) : undefined,
  };
});

const suppressDate = computed<string | undefined>(() => {
  return props.personne?.dateSuppression ? format(parseISO(props.personne.dateSuppression), 'P') : undefined;
});

const hasFunctions = computed<boolean>(() => {
  if (!props.personne) return false;
  return [
    CategoriePersonne.Enseignant.toString(),
    CategoriePersonne.Non_enseignant_etablissement.toString(),
    CategoriePersonne.Non_enseignant_collectivite_locale.toString(),
  ].includes(props.personne.categorie);
});

const isInfo2 = useSessionStorage<boolean>(`${__APP_SLUG__}.is-info2`, true);
</script>

<template>
  <v-card-text v-if="personne" class="pt-0">
    <div class="d-flex flex-row flex-wrap">
      <readonly-data v-admin label="uid" :value="personne.uid" class="modal-flex-item" />
      <readonly-data
        :label="t('person.information.profile')"
        :value="t(getCategoriePersonne(personne.categorie).i18n)"
        class="modal-flex-item"
      />
      <readonly-data :label="t('person.information.civility')" :value="personne.civilite" class="modal-flex-item" />
      <readonly-data :label="t('person.information.lastName')" :value="personne.patronyme" class="modal-flex-item" />
      <readonly-data :label="t('person.information.firstName')" :value="personne.givenName" class="modal-flex-item" />
      <readonly-data
        :label="t('person.information.birthDate')"
        :value="personne.dateNaissance ? format(parseISO(personne.dateNaissance), 'P') : undefined"
        class="modal-flex-item"
      />
      <readonly-data :label="t('person.information.email')" :value="personne.email" class="modal-flex-item" />
      <readonly-data :label="t('person.information.schoolYear')" :value="schoolYear" class="modal-flex-item" />
      <readonly-data :label="t('person.information.login')" class="modal-flex-item">
        <div class="d-flex flex-row align-center w-fit">
          <div>{{ login.i18n }}</div>
          <v-tooltip v-if="login.info != undefined" :text="login.info" location="bottom start">
            <template v-slot:activator="{ props }">
              <v-icon v-bind="props" icon="fas fa-circle-info" color="info" size="small" class="ml-2" />
            </template>
          </v-tooltip>
        </div>
      </readonly-data>
      <readonly-data :label="t('person.information.status')" class="modal-flex-item">
        <div class="d-flex flex-row align-center w-fit">
          <v-icon v-if="etat.color" icon="fas fa-circle" :color="etat.color" size="small" class="mr-2" />
          <div>{{ t(etat.i18n) }}</div>
          <v-tooltip
            v-if="suppressDate != undefined"
            :text="t('person.status.deletingDate', { suppressDate })"
            location="bottom start"
          >
            <template v-slot:activator="{ props }">
              <v-icon v-bind="props" icon="fas fa-circle-info" color="info" size="small" class="ml-2" />
            </template>
          </v-tooltip>
        </div>
      </readonly-data>
      <readonly-data label="Source" class="modal-flex-item">
        <div class="d-flex flex-row align-center w-fit">
          <v-icon :icon="getIcon(personne.source)" size="small" class="mr-2 text-medium-emphasis" />
          <div>{{ t('source.' + personne.source) }}</div>
        </div>
      </readonly-data>
      <readonly-data
        :label="t('person.information.sourceModificationDate')"
        :value="personne.dateSourceModification ? format(parseISO(personne.dateSourceModification), 'P') : undefined"
        class="modal-flex-item"
      />
    </div>
    <div v-if="hasFunctions">
      <div class="mb-3">
        <b>{{ t('person.information.function', 2) }}</b>
        <fonctions-layout :filieres="allFilieres" :fonctions="structureFonctions" class="my-0 px-1" />
      </div>
      <div class="mb-3">
        <div class="d-flex align-center">
          <b>{{ t('person.information.additionalFunction', 2) }}</b>
          <v-btn
            v-if="structureTab == Tabs.SchoolStaff && isCustomMapping && isEditAllowed(personne.etat)"
            color="primary"
            variant="tonal"
            density="compact"
            :text="t(hasStructureAdditionalFonctions ? 'button.edit' : 'button.add')"
            class="ml-2"
            @click="personneDialogState = PersonneDialogState.ManageAdditional"
          >
            <template #prepend>
              <v-icon :icon="hasStructureAdditionalFonctions ? 'fas fa-pen' : 'fas fa-plus'" size="sm" />
            </template>
          </v-btn>
        </div>
        <v-alert v-if="structureTab != Tabs.SchoolStaff" v-model="isInfo2" type="info" class="my-2" closable>
          Pour gérer les fonctions complémentaires de ce profil, veuillez utiliser
          <a href="/GLC" target="_self">GLC</a>.
        </v-alert>
        <fonctions-layout :filieres="allFilieres" :fonctions="structureAdditionalFonctions" class="my-0 px-1" />
      </div>
    </div>
  </v-card-text>
</template>
