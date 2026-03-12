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
import type { StructureRestriction } from '@/types/index.ts'
import { format } from 'date-fns'
import { storeToRefs } from 'pinia'
import { computed, ref, watchEffect } from 'vue'
import ManageRestrictionsDialog from '@/components/dialogs/ManageRestrictionsDialog.vue'
import { getRestrictions } from '@/services/api/index.ts'
import { useStructureStore } from '@/stores/index.ts'

const structureStore = useStructureStore()
const { initCurrentEtab } = structureStore
structureStore.init()
const { etabs, currentEtab } = storeToRefs(structureStore)

const selectedEtab = ref<number>()

const data = ref<StructureRestriction>()
const dataState = ref<'UNLOADED' | 'LOADING' | 'LOADED' | 'ERROR'>('UNLOADED')

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
    .filter(niveau => niveau.dateRentreeNiveau !== null || niveau.classes.length > 0)

  return { ...data.value, niveaux }
})

watchEffect(async (): Promise<void> => {
  if (selectedEtab.value === undefined)
    return

  initCurrentEtab(selectedEtab.value)
  dataState.value = 'LOADING'
  const response = await getRestrictions(selectedEtab.value)
  data.value = response
  dataState.value = response !== undefined ? 'LOADED' : 'ERROR'
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
</script>

<template>
  <v-container>
    <h1>Paramétrage de l'établissement</h1>

    <label for="tabs">Etablissement</label>
    <select
      id="tabs"
      v-model="selectedEtab"
    >
      <option
        v-for="etab in etabs"
        :key="etab.id"
        :value="etab.id"
      >
        {{ etab.nom }} - {{ etab.type }} {{ etab.uai }}
      </option>
    </select>

    <h2>Informations générales</h2>

    <div class="logo-card">
      <img
        v-if="currentEtab"
        :src="currentEtab.logo ?? '/annuaire_images/default_banner_v1.jpg'"
        alt="Logo de l'établissement"
      >
      <button
        class="btn-primary small"
        @click="() => {}"
      >
        Modifier
        <font-awesome-icon icon="fas fa-pen" />
      </button>
    </div>

    <div class="info-card">
      <div class="body">
        <ul v-if="currentEtab">
          <li>
            <h3>UAI</h3>
            <p>{{ currentEtab.uai }}</p>
          </li>
          <li>
            <h3>Siren</h3>
            <p>{{ currentEtab.siren }}</p>
          </li>
          <li>
            <h3>Etat</h3>
            <p>{{ currentEtab.etat }}</p>
          </li>
          <li>
            <h3>Etat alimentation</h3>
            <p>{{ currentEtab.etatAlim }}</p>
          </li>
          <li>
            <h3>Source</h3>
            <p>{{ currentEtab.source }}</p>
          </li>
          <li>
            <h3>Année scolaire</h3>
            <p>{{ currentEtab.anneeScolaire }}</p>
          </li>
          <li>
            <h3>Adresse</h3>
            <p>{{ currentEtab.adresse.adresse }}</p>
          </li>
          <li>
            <h3>Code postal</h3>
            <p>{{ currentEtab.adresse.codePostal }}</p>
          </li>
          <li>
            <h3>Ville</h3>
            <p>{{ currentEtab.adresse.ville }}</p>
          </li>
          <li>
            <h3>Boite postal</h3>
            <p>{{ currentEtab.adresse.boitePostale }}</p>
          </li>
          <li>
            <h3>Pays</h3>
            <p>{{ currentEtab.adresse.pays }}</p>
          </li>
          <li>
            <h3>Catégorie</h3>
            <p>{{ currentEtab.categorie }}</p>
          </li>
          <li>
            <h3>Mail</h3>
            <p>{{ currentEtab.mail }}</p>
          </li>
          <li>
            <h3>Nom</h3>
            <p>{{ currentEtab.nom }}</p>
          </li>
        </ul>
      </div>
    </div>

    <div class="info-card">
      <div class="body">
        <ul v-if="currentEtab">
          <li>
            <h3>Nom court</h3>
            <p>{{ currentEtab.nomCourt }}</p>
          </li>
          <li>
            <h3>Site web</h3>
            <p>{{ currentEtab.siteWeb }}</p>
          </li>
        </ul>
      </div>

      <footer>
        <button
          class="btn-primary small"
          @click="() => {}"
        >
          Modifier
          <font-awesome-icon icon="fas fa-pen" />
        </button>
      </footer>
    </div>

    <h2>Paramètres</h2>

    <div class="date-rentree-card">
      <header>
        <h3>Date de rentrée</h3>
      </header>

      <div class="body">
        <template v-if="dataToDisplay">
          <span>
            {{ toDisplayDate(dataToDisplay.dateRentreeEtab) }}
          </span>

          <div class="niveau-container">
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
        </template>
      </div>

      <footer>
        <button
          class="btn-primary small"
          @click="() => {
            dialogState = true
          }"
        >
          Modifier
          <font-awesome-icon icon="fas fa-pen" />
        </button>
      </footer>
    </div>

    <ManageRestrictionsDialog
      v-model="dialogState"
      :restrictions="data"
      @update:model-value="dialogState = false"
      @save="save"
    />
  </v-container>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.logo-card {
  flex: 0 0 auto;
  position: relative;
  overflow: hidden;
  aspect-ratio: 9 / 4;
  width: 100%;
  border-radius: 10px;
  box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);

  > img {
    position: absolute;
    inset: 0px;
    object-fit: cover;
    height: 100%;
    width: 100%;
  }

  > button {
    position: absolute;
    bottom: 16px;
    right: 16px;
  }
}

.info-card {
  display: flex;
  flex-direction: column;
  border-radius: 10px;
  box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
  padding: 16px;

  > .body {
    > ul {
      @include unstyled-list;
      display: flex;
      flex-direction: column;
      gap: 16px;

      > li {
        > * {
          margin-bottom: 0;
        }
      }
    }
  }

  > footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
  }
}

.date-rentree-card {
  display: flex;
  flex-direction: column;
  border-radius: 10px;
  box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
  padding: 16px;

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

  > footer {
    display: flex;
    justify-content: flex-end;
    margin-top: 24px;
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
