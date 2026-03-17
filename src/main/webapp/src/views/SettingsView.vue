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
import type { Etablissement, StructureRestriction } from '@/types/index.ts'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { storeToRefs } from 'pinia'
import { computed, ref, watchEffect } from 'vue'
import ManageRestrictionsDialog from '@/components/dialogs/ManageRestrictionsDialog.vue'
import PageLayout from '@/components/PageLayout.vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import StructureSearch from '@/components/search/structure/StructureSearch.vue'
import AdminSettings from '@/components/settings/AdminSettings.vue'
import ContactSettings from '@/components/settings/ContactSettings.vue'
import IdentitySettings from '@/components/settings/IdentitySettings.vue'
import LocalisationSettings from '@/components/settings/LocalisationSettings.vue'
import LogoSettings from '@/components/settings/LogoSettings.vue'
import { getEtablissement, getRestrictions } from '@/services/api/index.ts'
import { useStructureStore } from '@/stores/index.ts'

const structureStore = useStructureStore()
structureStore.init()
const { etabs } = storeToRefs(structureStore)

const selectedEtab = ref<number | undefined>(
  etabs.value
    ? etabs.value[0]?.id
    : undefined,
)

const currentEtab = ref<Etablissement | undefined>()
const data = ref<StructureRestriction | undefined>()

const dialogState = ref<boolean>(false)

const dataToDisplay = computed<StructureRestriction | undefined>(() => {
  if (!data.value)
    return

  const niveaux = data.value?.niveaux
    .map((niveau) => {
      const classes = niveau.classes
        .filter(({ dateRentreeClasse }) => dateRentreeClasse !== null)

      return { ...niveau, classes }
    })
    .filter(niveau => (
      niveau.dateRentreeNiveau !== null
      || niveau.classes.length > 0
    ))

  return { ...data.value, niveaux }
})

watchEffect(async (): Promise<void> => {
  if (selectedEtab.value === undefined)
    return

  currentEtab.value = await getEtablissement(selectedEtab.value)
  data.value = await getRestrictions(selectedEtab.value)
})

function toDisplayDate(
  date: string | undefined | null,
): string | undefined {
  return date
    ? format(date, 'P p')
    : undefined
}

function save() {

}

const canEdit = computed<boolean>(() => (
  currentEtab.value !== undefined
  && data.value !== undefined
))
</script>

<template>
  <div class="container">
    <PageLayout
      title="Paramétrage de l'établissement"
    >
      <StructureSearch
        v-model="selectedEtab"
        :search-list="etabs"
        variant="solo"
      />

      <div>
        <h2>Informations générales</h2>

        <div class="info-container">
          <LogoSettings :etab="currentEtab" />

          <IdentitySettings :etab="currentEtab" />

          <LocalisationSettings :etab="currentEtab" />

          <ContactSettings :etab="currentEtab" />

          <AdminSettings :etab="currentEtab" />
        </div>
      </div>

      <div>
        <h2>Paramètres</h2>

        <div class="r-card date-rentree-card">
          <header>
            <h3>Date de rentrée</h3>
          </header>

          <div class="body">
            <SafeEmptyData
              :value="toDisplayDate(dataToDisplay?.dateRentreeEtab)"
            />

            <div
              v-if="dataToDisplay"
              v-show="dataToDisplay.niveaux.length > 0"
              class="niveau-container"
            >
              <div
                v-for="niveau in dataToDisplay.niveaux"
                :key="niveau.niveau"
                class="niveau-card"
              >
                <h4>{{ niveau.niveau }}</h4>
                <span v-if="niveau.dateRentreeNiveau">
                  {{ toDisplayDate(niveau.dateRentreeNiveau) }}
                </span>

                <ul>
                  <li
                    v-for="classe in niveau.classes"
                    :key="classe.classe"
                    class="classe-card"
                  >
                    <span>
                      <b>
                        {{ classe.classe }}
                      </b>
                    </span>
                    <span>
                      {{ toDisplayDate(classe.dateRentreeClasse) }}
                    </span>
                  </li>
                </ul>
              </div>
            </div>
          </div>

          <footer
            v-if="canEdit"
          >
            <button
              class="btn-primary small"
              @click="() => {
                dialogState = true
              }"
            >
              Modifier
              <FontAwesomeIcon icon="fas fa-pen" />
            </button>
          </footer>
        </div>
      </div>
    </PageLayout>
  </div>

  <ManageRestrictionsDialog
    v-model="dialogState"
    :restrictions="data"
    @update:model-value="dialogState = false"
    @save="save"
  />
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.container {
  margin-top: 32px;
  margin-bottom: 40px;

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;
  }
}

.info-container {
  display: flex;
  flex-direction: column;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    align-items: start;

    > .full-width {
      grid-column: span 2;
    }
  }
}

.date-rentree-card {
  display: flex;
  flex-direction: column;

  > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > .niveau-container {
      display: grid;
      gap: 16px;

      > .niveau-card {
        display: flex;
        flex-direction: column;
        border-radius: 6px;
        border: 1px solid var(--#{$prefix}stroke);
        padding: 16px;

        > ul {
          @include unstyled-list;
          display: grid;
          gap: 16px;

          > .classe-card {
            display: flex;
            flex-direction: column;
            border-radius: 6px;
            border: 1px solid var(--#{$prefix}stroke);
            padding: 16px;
          }
        }
      }
    }
  }
}

@media (width >= map.get($grid-breakpoints, md)) {
  .date-rentree-card {
    > .body {
      > .niveau-container {
        grid-template-columns: repeat(auto-fill, minmax(512px, 1fr));

        > .niveau-card {
          > ul {
            grid-template-columns: repeat(auto-fill, minmax(256px, 1fr));
          }
        }
      }
    }
  }
}
</style>
