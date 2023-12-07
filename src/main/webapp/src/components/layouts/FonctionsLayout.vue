<script setup lang="ts">
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import type { Filiere } from '@/types/filiereType.ts';
import type { PersonneFonction } from '@/types/fonctionType.ts';
import { storeToRefs } from 'pinia';
import { ref, watch } from 'vue';

const configurationStore = useConfigurationStore();
const { currentStructureId } = storeToRefs(configurationStore);

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  fonctions: Array<PersonneFonction> | undefined;
}>();

const filteredFilieres = ref<Array<Filiere>>([]);

const filterFilieres = (): void => {
  if (props.filieres && props.fonctions) {
    const etabFonctions = props.fonctions.filter((fonction) => fonction.structure == currentStructureId.value);
    const filiereIds = [...new Set(etabFonctions.map((fonction) => fonction.filiere))];
    const disciplineIds = [...new Set(etabFonctions.map((fonction) => fonction.disciplinePoste))];

    filteredFilieres.value = props.filieres
      .filter((filiere) => filiereIds.includes(filiere.id))
      .map((filiere) => {
        const disciplines = filiere.disciplines.filter((discipline) => disciplineIds.includes(discipline.id));

        return { ...filiere, disciplines };
      });
  }
};

watch(
  () => props.fonctions,
  (newValue, oldValue) => {
    if (newValue != oldValue) {
      filterFilieres();
    }
  },
  { immediate: true },
);
</script>

<template>
  <v-row v-if="filteredFilieres.length > 0">
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
  <div v-else>
    <slot name="empty" />
  </div>
</template>
