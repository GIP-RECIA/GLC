<script setup lang="ts">
import PersonneCard from '@/components/PersonneCard.vue';
import type { Discipline } from '@/types/disciplineType.ts';
import type { Filiere } from '@/types/filiereType.ts';
import isEmpty from 'lodash.isempty';
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  accountStates?: Array<string>;
}>();

const filteredFilieres = ref<Array<Filiere>>([]);

const filterFilieres = () => {
  let filieres = props.filieres ? props.filieres : [];
  if (props.accountStates && props.accountStates.length > 0) {
    filieres = filieres.map((filiere) => {
      let disciplines: Array<Discipline> = filiere.disciplines.map((discipline) => {
        let personnes = !isEmpty(props.accountStates)
          ? discipline.personnes.filter((personne) => props.accountStates!.includes(personne.etat))
          : discipline.personnes;

        return { ...discipline, personnes };
      });
      disciplines = disciplines.filter((discipline) => discipline.personnes.length > 0);

      return { ...filiere, disciplines };
    });
  }
  filieres = filieres.filter((filiere) => filiere.disciplines.length > 0);

  filteredFilieres.value = filieres;
};

watch(
  () => props.filieres,
  (newValue, oldValue) => {
    if (newValue?.toString() != oldValue?.toString()) filterFilieres();
  },
  { immediate: true },
);

watch(
  () => props.accountStates,
  (newValue, oldValue) => {
    if (newValue?.toString() != oldValue?.toString()) filterFilieres();
  },
);
</script>

<template>
  <div v-if="filteredFilieres.length > 0">
    <transition-group>
      <div v-for="(filiere, index) in filteredFilieres" :key="index" class="pb-4">
        <div class="pb-2">{{ filiere.libelleFiliere }}</div>
        <v-row class="px-1">
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
                  <v-row class="px-1">
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
  <div v-else class="d-flex flex-column align-center justify-center pa-10">
    <v-icon icon="fas fa-filter-circle-xmark" size="x-large" />
    <div class="pt-2">{{ t('search.noResults') }}</div>
  </div>
</template>
