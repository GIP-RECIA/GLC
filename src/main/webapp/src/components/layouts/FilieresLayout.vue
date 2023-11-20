<script setup lang="ts">
import PersonneCard from '@/components/PersonneCard.vue';
import type { Discipline } from '@/types/disciplineType';
import type { Filiere } from '@/types/filiereType.ts';
import type { SimplePersonne } from '@/types/personneType';
import isEmpty from 'lodash.isempty';
import { onBeforeMount, ref, watch } from 'vue';

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  accountStates?: Array<string>;
}>();

const filteredFilieres = ref<Array<Filiere>>([]);

onBeforeMount(() => {
  if (props.filieres && props.filieres.length > 0) filteredFilieres.value = getFiliere();
});

watch(
  () => props.filieres,
  (oldValue, newValue) => {
    if (newValue != oldValue) filteredFilieres.value = getFiliere();
  },
);

watch(
  () => props.accountStates,
  (newValue, oldValue) => {
    if (newValue != oldValue) filteredFilieres.value = getFiliere();
  },
);

const getPersonnesByState = (discipline: Discipline): Array<SimplePersonne> => {
  return !isEmpty(props.accountStates)
    ? discipline.personnes.filter((personne) => props.accountStates!.includes(personne.etat))
    : discipline.personnes;
};

const getDisciplinesWithPersonnes = (filiere: Filiere): Array<Discipline> => {
  let disciplines: Array<Discipline> = filiere.disciplines.map((discipline) => {
    let personnes = getPersonnesByState(discipline);

    return { ...discipline, personnes };
  });
  disciplines = disciplines.filter((discipline) => discipline.personnes.length > 0);

  return disciplines;
};

const getFiliere = (): Array<Filiere> => {
  let filieres = props.filieres ? props.filieres : [];
  filieres = filieres.map((filiere) => {
    let disciplines = getDisciplinesWithPersonnes(filiere);

    return { ...filiere, disciplines };
  });
  filieres = filieres.filter((filiere) => filiere.disciplines.length > 0);

  return filieres;
};
</script>

<template>
  <div v-if="filieres && filieres.length > 0">
    <transition-group>
      <div v-for="(filiere, index) in filteredFilieres" :key="index" class="pb-4">
        <div class="pb-2">
          {{ filiere.libelleFiliere }}
        </div>
        <v-row>
          <transition-group>
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
                  <v-row>
                    <transition-group>
                      <v-col
                        v-for="(personne, index) in discipline.personnes"
                        :key="index"
                        :cols="6"
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
</template>
