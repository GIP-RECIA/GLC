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
import type { SimplePersonne } from '@/types'
import { storeToRefs } from 'pinia'
import { computed, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { useDisplay } from 'vuetify'
import AlertManager from '@/components/AlertManager.vue'
import CustomPagination from '@/components/CustomPagination.vue'
import InfoGrid from '@/components/info/InfoGrid.vue'
import PersonneCard from '@/components/PersonneCard.vue'
import ReadonlyData from '@/components/ReadonlyData.vue'
import { useStructureStore } from '@/stores'
import { DashboardPanel, Etat } from '@/types/enums'

const isDev = import.meta.env.DEV

const structureStore = useStructureStore()
const { currentEtab, personnesByEtat } = storeToRefs(structureStore)

const { t } = useI18n()
const { name } = useDisplay()

const pageItems = ref<Array<SimplePersonne> | undefined>()
const pageItems2 = ref<Array<SimplePersonne> | undefined>()
const pageItems3 = ref<Array<SimplePersonne> | undefined>()

const itemsPerPage = computed<number>(() => {
  const defaultItemsPerPage = 10

  switch (name.value) {
    case 'xs':
      return defaultItemsPerPage
    case 'sm':
      return 2 * defaultItemsPerPage
    case 'md':
      return 3 * defaultItemsPerPage
    case 'lg':
      return 4 * defaultItemsPerPage
    case 'xl':
      return 4 * defaultItemsPerPage
    case 'xxl':
      return 6 * defaultItemsPerPage
    default:
      return defaultItemsPerPage
  }
})

const panel = ref<Array<DashboardPanel>>([DashboardPanel.DeletingAccounts])
</script>

<template>
  <v-container fluid class="d-flex flex-column ga-4">
    <AlertManager />

    <v-card v-if="isDev && currentEtab" flat>
      <v-card-text class="info-container">
        <ReadonlyData v-admin label="uai" :value="currentEtab.uai" />
        <ReadonlyData label="etat" :value="currentEtab.etat" />
        <ReadonlyData label="etatAlim" :value="currentEtab.etatAlim" />
        <ReadonlyData label="source" :value="currentEtab.source" />
        <ReadonlyData label="anneeScolaire" :value="currentEtab.anneeScolaire" />
        <ReadonlyData label="adresse" :value="currentEtab.adresse.adresse" />
        <ReadonlyData label="codePostal" :value="currentEtab.adresse.codePostal" />
        <ReadonlyData label="ville" :value="currentEtab.adresse.ville" />
        <ReadonlyData label="boitePostale" :value="currentEtab.adresse.boitePostale" />
        <ReadonlyData label="pays" :value="currentEtab.adresse.pays" />
        <ReadonlyData label="categorie" :value="currentEtab.categorie" />
        <ReadonlyData label="mail" :value="currentEtab.mail" />
        <ReadonlyData label="nom" :value="currentEtab.nom" />
        <ReadonlyData label="nomCourt" :value="currentEtab.nomCourt" />
        <ReadonlyData label="siren" :value="currentEtab.siren" />
        <ReadonlyData label="siteWeb" :value="currentEtab.siteWeb" />
        <ReadonlyData label="modeleLogin" :value="currentEtab.modeleLogin" />
        <ReadonlyData label="logo" :value="currentEtab.logo" />
      </v-card-text>
    </v-card>

    <InfoGrid />

    <v-expansion-panels v-model="panel">
      <v-expansion-panel :value="DashboardPanel.DeletingAccounts" :elevation="0" rounded="lg">
        <v-expansion-panel-title>
          <div class="expansion-title">
            {{ `${t('deletingAccounts')} (${personnesByEtat.get(Etat.Deleting)?.length ?? 0})` }}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <div v-if="pageItems && pageItems.length > 0" class="container">
            <PersonneCard v-for="personne in pageItems" :key="personne.id" variant="tonal" :personne="personne" />
          </div>
          <CustomPagination
            :items="personnesByEtat.get(Etat.Deleting)"
            :items-per-page="itemsPerPage"
            hide-single-page
            class="mt-4"
            @update:page="(items) => (pageItems = items)"
          />
        </v-expansion-panel-text>
      </v-expansion-panel>

      <v-expansion-panel :value="DashboardPanel.DeletedAccounts" :elevation="0" rounded="lg">
        <v-expansion-panel-title>
          <div class="expansion-title">
            {{ `${t('deletedAccounts')} (${personnesByEtat.get(Etat.Delete)?.length ?? 0})` }}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <div v-if="pageItems2 && pageItems2.length > 0" class="container">
            <PersonneCard v-for="personne in pageItems2" :key="personne.id" variant="tonal" :personne="personne" />
          </div>
          <CustomPagination
            :items="personnesByEtat.get(Etat.Delete)"
            :items-per-page="itemsPerPage"
            hide-single-page
            class="mt-4"
            @update:page="(items) => (pageItems2 = items)"
          />
        </v-expansion-panel-text>
      </v-expansion-panel>

      <v-expansion-panel :value="DashboardPanel.WithoutFunctions" :elevation="0" rounded="lg">
        <v-expansion-panel-title>
          <div class="expansion-title">
            {{
              `${t('withoutFunctions')} (${currentEtab?.withoutFunctions ? currentEtab.withoutFunctions.length : 0})`
            }}
          </div>
        </v-expansion-panel-title>
        <v-expansion-panel-text>
          <div v-if="pageItems3 && pageItems3.length > 0" class="container">
            <PersonneCard v-for="personne in pageItems3" :key="personne.id" variant="tonal" :personne="personne" />
          </div>
          <CustomPagination
            :items="currentEtab?.withoutFunctions"
            :items-per-page="itemsPerPage"
            hide-single-page
            class="mt-4"
            @update:page="(items) => (pageItems3 = items)"
          />
        </v-expansion-panel-text>
      </v-expansion-panel>
    </v-expansion-panels>
  </v-container>
</template>

<style scoped lang="scss">
.info-container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;

  @media (width >= 768px) {
    grid-template-columns: 1fr 1fr;
  }

  @media (width >= 960px) {
    grid-template-columns: 1fr 1fr 1fr;
  }

  @media (width >= 1280px) {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }

  @media (width >= 2560px) {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
  }
}

.container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;
  // grid-auto-rows: 1fr;

  @media (width >= 768px) {
    grid-template-columns: 1fr 1fr;
  }

  @media (width >= 960px) {
    grid-template-columns: 1fr 1fr 1fr;
  }

  @media (width >= 1280px) {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }

  @media (width >= 2560px) {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
  }
}

.expansion-title {
  font-size: 0.875rem;
  font-weight: 400;
  letter-spacing: 0.0178571429em;
  opacity: var(--v-medium-emphasis-opacity);
}
</style>

<style lang="scss">
.v-expansion-panel-title__overlay {
  opacity: 0 !important;
}

.v-expansion-panel-text__wrapper {
  padding-left: 16px !important;
  padding-right: 16px !important;
}
</style>
