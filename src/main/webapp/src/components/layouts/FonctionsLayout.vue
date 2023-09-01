<script setup lang="ts">
import { useConfigurationStore } from '@/stores/configurationStore';
import type { Filiere } from '@/types/filiereType';
import type { PersonneFonction } from '@/types/fonctionType';
import { storeToRefs } from 'pinia';
import { onBeforeMount, ref, unref, watch } from 'vue';

const configurationStore = useConfigurationStore();
const { currentStructureId } = storeToRefs(configurationStore);

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  fonctions: Array<PersonneFonction> | undefined;
}>();

const filteredFilieres = ref<Array<Filiere>>([]);

onBeforeMount(() => {
  if (props.fonctions && props.fonctions.length > 0) filterFilieres();
});

watch(
  () => props.fonctions,
  (newValue, oldValue) => {
    if (newValue != oldValue) {
      filterFilieres();
    }
  },
);

const filterFilieres = (): void => {
  const etabFonctions = props.fonctions
    ? props.fonctions.filter((fonction) => fonction.structure == currentStructureId.value)
    : [];
  const filiereIds = [...new Set(etabFonctions.map((fonction) => fonction.filiere))];
  const disciplineIds = [...new Set(etabFonctions.map((fonction) => fonction.disciplinePoste))];

  let filterFilieres = unref(props.filieres);

  filterFilieres = filterFilieres
    ?.filter((filiere) => filiereIds.includes(filiere.id))
    .map((filiere) => {
      const disciplines = filiere.disciplines.filter((discipline) => disciplineIds.includes(discipline.id));

      return { ...filiere, disciplines };
    });

  filteredFilieres.value = filterFilieres ? filterFilieres : [];
};
</script>

<template>
  <div v-if="fonctions && fonctions.length > 0 && filteredFilieres.length > 0">
    <v-row>
      <v-col v-for="(filiere, index) in filteredFilieres" :key="index" :cols="12" :md="6" class="pa-2">
        <v-card :subtitle="filiere.libelleFiliere" variant="tonal" min-height="100%">
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
