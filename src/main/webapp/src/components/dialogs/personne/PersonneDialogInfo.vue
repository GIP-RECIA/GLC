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
import type { Personne, PersonneFonction } from '@/types'
import FonctionsLayout from '@/components/layouts/FonctionsLayout.vue'
import ReadonlyData from '@/components/ReadonlyData.vue'
import { usePersonne } from '@/composables'
import { useConfigurationStore, usePersonneStore } from '@/stores'
import { PersonneDialogState, Tabs } from '@/types/enums'
import { getCategoriePersonne, getIcon } from '@/utils'
import { useSessionStorage } from '@vueuse/core'
import { format } from 'date-fns'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n'

defineProps<{
  personne?: Personne
}>()
const configurationStore = useConfigurationStore()
const { allFilieres, structureTab } = storeToRefs(configurationStore)

const personneStore = usePersonneStore()
const { personneStructure, dialogState, editFunction } = storeToRefs(personneStore)

const { t } = useI18n()

const { etat, schoolYear, login, suppressDate, hasFunctions, canEditAdditionals } = usePersonne()

const isInfo2 = useSessionStorage<boolean>(`${__APP_SLUG__}.is-info2`, true)

function addFonction(): void {
  editFonction(undefined)
}

function editFonction(payload: PersonneFonction | undefined): void {
  if (payload)
    editFunction.value = payload
  dialogState.value = PersonneDialogState.ManageAdditionalMultiple
}
</script>

<template>
  <v-card-text v-if="personne" class="pt-0">
    <div class="container">
      <ReadonlyData v-admin label="UID" :value="personne.uid" />
      <ReadonlyData
        :label="t('person.information.profile')"
        :value="t(getCategoriePersonne(personne.categorie).i18n)"
      />
      <ReadonlyData :label="t('person.information.civility')" :value="personne.civilite" />
      <ReadonlyData :label="t('person.information.lastName')" :value="personne.patronyme" />
      <ReadonlyData :label="t('person.information.firstName')" :value="personne.givenName" />
      <ReadonlyData
        :label="t('person.information.birthDate')"
        :value="personne.dateNaissance ? format(personne.dateNaissance, 'P') : undefined"
      />
      <ReadonlyData :label="t('person.information.email')" :value="personne.email" />
      <ReadonlyData :label="t('person.information.schoolYear')" :value="schoolYear" />
      <ReadonlyData :label="t('person.information.login')">
        <div class="d-flex flex-row align-center w-fit">
          <div>{{ login.i18n }}</div>
          <v-tooltip v-if="login.info !== undefined" :text="login.info" location="bottom start">
            <template #activator="{ props }">
              <v-icon v-bind="props" icon="fas fa-circle-info" color="info" size="small" class="ms-2" />
            </template>
          </v-tooltip>
        </div>
      </ReadonlyData>
      <ReadonlyData :label="t('person.information.status')">
        <div class="d-flex flex-row align-center w-fit">
          <v-icon v-if="etat.color" icon="fas fa-circle" :color="etat.color" size="small" class="me-2" />
          <div>{{ t(etat.i18n) }}</div>
          <v-tooltip
            v-if="suppressDate !== undefined"
            :text="t('person.status.deletingDate', { suppressDate })"
            location="bottom start"
          >
            <template #activator="{ props }">
              <v-icon v-bind="props" icon="fas fa-circle-info" color="info" size="small" class="ms-2" />
            </template>
          </v-tooltip>
        </div>
      </ReadonlyData>
      <ReadonlyData label="Source">
        <div class="d-flex flex-row align-center w-fit">
          <v-icon :icon="getIcon(personne.source)" size="small" class="me-2 text-medium-emphasis" />
          <div>{{ t(`source.${personne.source}`) }}</div>
        </div>
      </ReadonlyData>
      <ReadonlyData
        :label="t('person.information.sourceModificationDate')"
        :value="personne.dateSourceModification ? format(personne.dateSourceModification, 'P') : undefined"
      />
      <template v-if="hasFunctions">
        <div class="full-width">
          <div class="mb-1">
            <b>{{ t('person.information.function', 2) }}</b>
          </div>
          <FonctionsLayout :filieres="allFilieres" :fonctions="personneStructure.fonctions" />
        </div>
        <div class="full-width">
          <div class="title-actions">
            <div class="mb-1">
              <b>{{ t('person.information.additionalFunction', 2) }}</b>
            </div>
            <div class="d-flex align-center gc-2 mb-2">
              <v-btn
                v-if="structureTab === Tabs.School && canEditAdditionals"
                color="primary"
                variant="tonal"
                density="compact"
                :text="t(personneStructure.additionalFonctions ? 'button.edit' : 'button.add')"
                @click="addFonction"
              >
                <template #prepend>
                  <v-icon :icon="personneStructure.additionalFonctions ? 'fas fa-pen' : 'fas fa-plus'" size="sm" />
                </template>
              </v-btn>
            </div>
          </div>
          <div class="d-flex flex-column ga-2">
            <v-alert
              v-if="structureTab !== Tabs.School"
              v-model="isInfo2"
              type="info"
              variant="tonal"
              rounded="lg"
              closable
            >
              Pour gérer les fonctions complémentaires de ce profil, veuillez utiliser
              <a href="/GLC" target="_self">GLC</a>.
            </v-alert>
            <FonctionsLayout
              :filieres="allFilieres"
              :fonctions="personneStructure.additionalFonctions"
              :clickable="structureTab === Tabs.School && canEditAdditionals"
              @item-clic="editFonction"
            />
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

.title-actions {
  display: flex;
  flex-direction: column;
  row-gap: 4px;
  column-gap: 8px;

  @media (width >= 700px) {
    flex-direction: row;
  }
}
</style>
