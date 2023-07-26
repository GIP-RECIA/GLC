<script setup lang="ts">
import { useFonctionStore } from "@/stores/fonctionStore";
import type { Filiere } from "@/types/filiereType";
import type { PersonneFonction } from "@/types/fonctionType";
import { storeToRefs } from "pinia";
import { unref, ref, onBeforeMount } from "vue";

const fonctionStore = useFonctionStore();
const { filieres } = storeToRefs(fonctionStore);

const props = defineProps<{
  fonctions: Array<PersonneFonction>;
}>();

const filteredFilieres = ref<Array<Filiere>>([]);

onBeforeMount(() => {
  if (props.fonctions.length > 0) filterFilieres();
});

const filterFilieres = (): void => {
  const filiereIds = [
    ...new Set(props.fonctions.map((fonction) => fonction.filiere)),
  ];
  const disciplineIds = [
    ...new Set(props.fonctions.map((fonction) => fonction.disciplinePoste)),
  ];

  let filterFilieres = unref(filieres);

  filterFilieres = filterFilieres
    ?.filter((filiere) => filiereIds.includes(filiere.id))
    .map((filiere) => {
      const disciplines = filiere.disciplines.filter((discipline) =>
        disciplineIds.includes(discipline.id)
      );

      return { ...filiere, disciplines };
    });

  filteredFilieres.value = filterFilieres ? filterFilieres : [];
};
</script>

<template>
  <div v-if="fonctions.length > 0">
    <v-row>
      <v-col
        v-for="(filiere, index) in filteredFilieres"
        :key="index"
        :cols="12"
        :md="6"
      >
        <v-card :subtitle="filiere.libelleFiliere" variant="tonal">
          <v-card-text class="pb-2 mt--3">
            <div class="v-chip-group v-chip-group--column v-theme--light">
              <v-chip
                v-for="(discipline, index) in filiere.disciplines"
                :key="index"
                :text="discipline.disciplinePoste"
                color="primary"
                rounded
              />
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </div>
  <div v-else>-</div>
</template>
