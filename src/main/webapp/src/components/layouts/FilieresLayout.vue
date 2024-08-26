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
import PersonneCard from '@/components/PersonneCard.vue';
import type { Discipline } from '@/types/disciplineType.ts';
import type { Filiere } from '@/types/filiereType.ts';
import isEmpty from 'lodash.isempty';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  accountStates?: Array<string>;
}>();

const filteredFilieres = computed<Array<Filiere>>(() => {
  let filieres = props.filieres ? props.filieres : [];
  if (!isEmpty(props.accountStates)) {
    filieres = filieres.map((filiere) => {
      const disciplines: Array<Discipline> = filiere.disciplines
        .map((discipline) => {
          const personnes = discipline.personnes.filter((personne) => props.accountStates!.includes(personne.etat));

          return { ...discipline, personnes };
        })
        .filter((discipline) => discipline.personnes.length > 0);

      return { ...filiere, disciplines };
    });
  }
  filieres = filieres.filter((filiere) => filiere.disciplines.length > 0);

  return filieres;
});
</script>

<template>
  <div v-if="filteredFilieres.length > 0" class="container">
    <div v-for="filiere in filteredFilieres" :key="filiere.codeFiliere">
      <div class="text-uppercase pb-2">
        {{ filiere.codeFiliere != '-' ? filiere.libelleFiliere : t('withoutFunctions') }}
      </div>
      <div class="discipline-container">
        <v-card
          v-for="discipline in filiere.disciplines"
          :key="discipline.code"
          :subtitle="discipline.code != '-' ? discipline.disciplinePoste : ''"
          flat
          min-height="100%"
        >
          <v-card-text>
            <div class="personne-container">
              <personne-card
                v-for="personne in discipline.personnes"
                :key="personne.id"
                variant="tonal"
                :personne="personne"
              />
            </div>
          </v-card-text>
        </v-card>
      </div>
    </div>
  </div>
  <div v-else class="d-flex flex-column align-center justify-center pa-10">
    <v-icon icon="fas fa-filter-circle-xmark" size="x-large" />
    <div class="pt-2">{{ t('search.noResults') }}</div>
  </div>
</template>

<style scoped lang="scss">
.container {
  display: grid;
  gap: 1em;
  grid-template-columns: 1fr;
}

.discipline-container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;

  @media (width >= 960px) {
    grid-template-columns: 1fr 1fr;
  }

  @media (width >= 1280px) {
    grid-template-columns: 1fr 1fr 1fr;
  }

  @media (width >= 1920px) {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }

  @media (width >= 2560px) {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
  }
}

.personne-container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;
  // grid-auto-rows: 1fr;

  @media (width >= 960px) {
    grid-template-columns: 1fr 1fr;
  }
}
</style>

<style lang="scss">
.discipline-container > .v-card .v-card-subtitle {
  padding: 0 !important;
}
</style>
