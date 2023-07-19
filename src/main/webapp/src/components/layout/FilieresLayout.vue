<script setup lang="ts">
import PersonneCard from "@/components/PersonneCard.vue";
import type { Filiere } from "@/types/filiereType";
import { watch, onBeforeMount, unref, ref } from "vue";

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  showAll: boolean;
}>();

const filteredFilieres = ref<Array<Filiere>>([]);

onBeforeMount(() => {
  if (props.filieres && props.filieres.length > 0) filterFiliere();
});

watch(
  () => props.filieres,
  (oldValue, newValue) => {
    if (newValue != oldValue) filterFiliere();
  }
);

watch(
  () => props.showAll,
  (newValue, oldValue) => {
    if (newValue != oldValue) filterFiliere();
  }
);

const filterFiliere = (): void => {
  let filterFiliere = unref(props.filieres);

  if (!unref(props.showAll)) {
    filterFiliere = filterFiliere
      ?.map((filiere) => {
        const disciplines = filiere.disciplines.filter(
          (discipline) => discipline.personnes.length > 0
        );

        return { ...filiere, disciplines };
      })
      .filter((filiere) => filiere.disciplines.length > 0);
  }

  filteredFilieres.value = filterFiliere ? filterFiliere : [];
};
</script>

<template>
  <div v-if="filieres && filieres.length > 0">
    <div v-for="(filiere, index) in filteredFilieres" :key="index" class="pb-4">
      <div class="pb-2">
        {{ filiere.libelleFiliere }}
      </div>
      <v-row>
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
  </div>
</template>
