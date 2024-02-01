<script setup lang="ts">
import PersonneCard from '@/components/PersonneCard.vue';
import type { Discipline } from '@/types/disciplineType.ts';
import type { Filiere } from '@/types/filiereType.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import isEmpty from 'lodash.isempty';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  withoutFunctions: Array<SimplePersonne> | undefined;
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

const filteredWithoutFunctions = computed(() => {
  let withoutFunctions = props.withoutFunctions ? props.withoutFunctions : [];
  if (!isEmpty(props.accountStates)) {
    withoutFunctions = withoutFunctions.filter((personne) => props.accountStates!.includes(personne.etat));
  }

  return withoutFunctions;
});
</script>

<template>
  <div v-show="filteredFilieres.length > 0">
    <transition-group name="custom">
      <div v-for="(filiere, index) in filteredFilieres" :key="index" class="pb-4">
        <div class="pb-2">{{ filiere.libelleFiliere }}</div>
        <v-row class="px-1">
          <transition-group name="custom">
            <v-col
              v-for="(discipline, index) in filiere.disciplines"
              :key="index"
              :cols="12"
              :md="6"
              :lg="4"
              :xxl="3"
              class="pa-2"
            >
              <v-card :subtitle="discipline.disciplinePoste" flat min-height="100%">
                <v-card-text>
                  <v-row class="px-1 pb-1">
                    <transition-group name="custom">
                      <v-col
                        v-for="(personne, index) in discipline.personnes"
                        :key="index"
                        :cols="12"
                        :sm="6"
                        class="d-flex align-center pa-2"
                      >
                        <personne-card variant="tonal" :personne="personne" />
                      </v-col>
                    </transition-group>
                  </v-row>
                </v-card-text>
              </v-card>
            </v-col>
          </transition-group>
        </v-row>
      </div>
    </transition-group>
  </div>

  <div v-show="filteredWithoutFunctions.length > 0">
    <div class="text-uppercase pb-2">{{ t('withoutFunctions') }}</div>
    <v-card flat>
      <v-card-text>
        <v-row class="pa-1">
          <transition-group name="custom">
            <v-col
              v-for="(personne, index) in filteredWithoutFunctions"
              :key="index"
              :cols="12"
              :sm="6"
              :md="3"
              :lg="2"
              class="d-flex align-center pa-2"
            >
              <personne-card variant="tonal" :personne="personne" />
            </v-col>
          </transition-group>
        </v-row>
      </v-card-text>
    </v-card>
  </div>

  <div
    v-if="filteredFilieres.length == 0 && filteredWithoutFunctions.length == 0"
    class="d-flex flex-column align-center justify-center pa-10"
  >
    <v-icon icon="fas fa-filter-circle-xmark" size="x-large" />
    <div class="pt-2">{{ t('search.noResults') }}</div>
  </div>
</template>
