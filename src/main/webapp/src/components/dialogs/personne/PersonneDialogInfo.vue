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
import { usePersonne } from '@/composables/usePersonne';
import { useConfigurationStore, usePersonneStore } from '@/stores/index.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import type { Personne } from '@/types/personneType.ts';
import { getCategoriePersonne, getIcon } from '@/utils/accountUtils.ts';
import { useSessionStorage } from '@vueuse/core';
import { format } from 'date-fns';
import { storeToRefs } from 'pinia';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { allFilieres, structureTab, personneDialogState } = storeToRefs(configurationStore);

const personneStore = usePersonneStore();
const { personneStructure } = storeToRefs(personneStore);

const { t } = useI18n();

defineProps<{
  personne?: Personne;
}>();

const { etat, schoolYear, login, suppressDate, hasFunctions, canEditAdditionals } = usePersonne();

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
          <fonctions-layout :filieres="allFilieres" :fonctions="personneStructure.fonctions" />
        </div>
        <div class="full-width">
          <div class="d-flex align-center mb-1">
            <b>{{ t('person.information.additionalFunction', 2) }}</b>
            <v-btn
              v-if="structureTab == Tabs.SchoolStaff && canEditAdditionals"
              color="primary"
              variant="tonal"
              density="compact"
              :text="t(personneStructure.additionalFonctions ? 'button.edit' : 'button.add')"
              class="ms-2 mb-1"
              @click="personneDialogState = PersonneDialogState.ManageAdditional"
            >
              <template #prepend>
                <v-icon :icon="personneStructure.additionalFonctions ? 'fas fa-pen' : 'fas fa-plus'" size="sm" />
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
            <fonctions-layout :filieres="allFilieres" :fonctions="personneStructure.additionalFonctions" />
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
