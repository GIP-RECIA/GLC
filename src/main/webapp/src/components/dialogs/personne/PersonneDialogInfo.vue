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
import ReadonlyData from '@/components/ReadonlyData.vue';
import FonctionsLayout from '@/components/layouts/FonctionsLayout.vue';
import { useConfigurationStore, usePersonneStore, useStructureStore } from '@/stores/index.ts';
import type { enumValues } from '@/types/enumValuesType.ts';
import { CategoriePersonne } from '@/types/enums/CategoriePersonne.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import type { Personne } from '@/types/personneType.ts';
import { getCategoriePersonne, getEtat, getIcon } from '@/utils/accountUtils.ts';
import { useSessionStorage } from '@vueuse/core';
import { format, getYear } from 'date-fns';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { isEditAllowed, getLoginOffice } = configurationStore;
const { allFilieres, structureTab, personneDialogState } = storeToRefs(configurationStore);

const stcuctureStore = useStructureStore();
const { fonction } = storeToRefs(stcuctureStore);

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
    const year = getYear(props.personne.anneeScolaire);
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
  return props.personne?.dateSuppression ? format(props.personne.dateSuppression, 'P') : undefined;
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
    <div class="container">
      <readonly-data v-admin label="uid" :value="personne.uid" />
      <readonly-data
        :label="t('person.information.profile')"
        :value="t(getCategoriePersonne(personne.categorie).i18n)"
      />
      <readonly-data :label="t('person.information.civility')" :value="personne.civilite" />
      <readonly-data :label="t('person.information.lastName')" :value="personne.patronyme" />
      <readonly-data :label="t('person.information.firstName')" :value="personne.givenName" />
      <readonly-data
        :label="t('person.information.birthDate')"
        :value="personne.dateNaissance ? format(personne.dateNaissance, 'P') : undefined"
      />
      <readonly-data :label="t('person.information.email')" :value="personne.email" />
      <readonly-data :label="t('person.information.schoolYear')" :value="schoolYear" />
      <readonly-data :label="t('person.information.login')">
        <div class="d-flex flex-row align-center w-fit">
          <div>{{ login.i18n }}</div>
          <v-tooltip v-if="login.info != undefined" :text="login.info" location="bottom start">
            <template v-slot:activator="{ props }">
              <v-icon v-bind="props" icon="fas fa-circle-info" color="info" size="small" class="ms-2" />
            </template>
          </v-tooltip>
        </div>
      </readonly-data>
      <readonly-data :label="t('person.information.status')">
        <div class="d-flex flex-row align-center w-fit">
          <v-icon v-if="etat.color" icon="fas fa-circle" :color="etat.color" size="small" class="me-2" />
          <div>{{ t(etat.i18n) }}</div>
          <v-tooltip
            v-if="suppressDate != undefined"
            :text="t('person.status.deletingDate', { suppressDate })"
            location="bottom start"
          >
            <template v-slot:activator="{ props }">
              <v-icon v-bind="props" icon="fas fa-circle-info" color="info" size="small" class="ms-2" />
            </template>
          </v-tooltip>
        </div>
      </readonly-data>
      <readonly-data label="Source">
        <div class="d-flex flex-row align-center w-fit">
          <v-icon :icon="getIcon(personne.source)" size="small" class="me-2 text-medium-emphasis" />
          <div>{{ t('source.' + personne.source) }}</div>
        </div>
      </readonly-data>
      <readonly-data
        :label="t('person.information.sourceModificationDate')"
        :value="personne.dateSourceModification ? format(personne.dateSourceModification, 'P') : undefined"
      />
      <template v-if="hasFunctions">
        <div class="full-width">
          <div class="mb-1">
            <b>{{ t('person.information.function', 2) }}</b>
          </div>
          <fonctions-layout :filieres="allFilieres" :fonctions="structureFonctions" />
        </div>
        <div class="full-width">
          <div class="d-flex align-center mb-1">
            <b>{{ t('person.information.additionalFunction', 2) }}</b>
            <v-btn
              v-if="structureTab == Tabs.SchoolStaff && fonction?.customMapping && isEditAllowed(personne.etat)"
              color="primary"
              variant="tonal"
              density="compact"
              :text="t(hasStructureAdditionalFonctions ? 'button.edit' : 'button.add')"
              class="ms-2 mb-1"
              @click="personneDialogState = PersonneDialogState.ManageAdditional"
            >
              <template #prepend>
                <v-icon :icon="hasStructureAdditionalFonctions ? 'fas fa-pen' : 'fas fa-plus'" size="sm" />
              </template>
            </v-btn>
          </div>
          <div class="d-flex flex-column ga-2">
            <v-alert
              v-if="structureTab != Tabs.SchoolStaff"
              v-model="isInfo2"
              type="info"
              variant="tonal"
              rounded="lg"
              closable
            >
              Pour gérer les fonctions complémentaires de ce profil, veuillez utiliser
              <a href="/GLC" target="_self">GLC</a>.
            </v-alert>
            <fonctions-layout :filieres="allFilieres" :fonctions="structureAdditionalFonctions" />
          </div>
        </div>
      </template>
    </div>
  </v-card-text>
</template>

<style scoped lang="scss">
.container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;

  @media (width >= 700px) {
    grid-template-columns: 1fr 1fr;

    > .full-width {
      grid-column: span 2;
    }
  }
}
</style>
