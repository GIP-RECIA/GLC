<script setup lang="ts">
import PersonneCard from "@/components/PersonneCard.vue";
import type { Discipline } from "@/types/disciplineType";
import type { Filiere } from "@/types/filiereType";
import { watch, onBeforeMount, unref, ref } from "vue";

const props = defineProps<{
  filiere: Filiere;
  showAll: boolean;
}>();

const disciplines = ref<Array<Discipline>>([]);

onBeforeMount(() => {
  filteredDisciplines();
});

watch(
  () => props.showAll,
  (newValue, oldValue) => {
    if (newValue != oldValue) {
      filteredDisciplines();
    }
  }
);

watch(
  () => props.filiere,
  (newValue, oldValue) => {
    if (newValue != oldValue) {
      filteredDisciplines();
    }
  }
);

const filteredDisciplines = (): void => {
  const filteredDisciplines = unref(props.filiere.disciplines);

  disciplines.value = unref(props.showAll)
    ? filteredDisciplines
    : filteredDisciplines.filter(
        (discipline) => discipline.personnes.length > 0
      );
};
</script>

<template>
  <div v-if="disciplines.length > 0" class="pb-4">
    <div class="pb-2">
      {{ filiere.libelleFiliere }}
    </div>
    <v-row>
      <v-col
        v-for="(discipline, index) in disciplines"
        :key="index"
        :cols="12"
        :md="6"
        :lg="4"
        :xxl="3"
        class="pa-2"
      >
        <v-card
          v-if="!showAll ? discipline.personnes.length > 0 : true"
          :subtitle="discipline.disciplinePoste"
          flat
          min-height="100%"
        >
          <v-card-text>
            <v-row>
              <v-col
                :cols="6"
                v-for="(personne, index) in discipline.personnes"
                :key="index"
                class="pa-2"
              >
                <personne-card variant="tonal" :personne="personne" />
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
</template>
