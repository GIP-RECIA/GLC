import { useStructureStore } from "./structureStore";
import { getFonctions } from "@/services/fonctionService";
import type { Filiere } from "@/types/filiereType";
import type { Fonction } from "@/types/fonctionType";
import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useFonctionStore = defineStore("fonctions", () => {
  const structureStore = useStructureStore();

  const fonctions = ref<Fonction | undefined>();

  const filieres = computed((): Array<Filiere> | undefined =>
    fonctions.value
      ? fonctions.value["AC-ORLEANS-TOURS"].filiereWithDiscipline
      : undefined
  );

  const init = async (): Promise<void> => {
    fonctions.value = (await getFonctions()).data.payload;
  };

  const currentEtabFilieres = computed((): Array<Filiere> | undefined => {
    const filieresInEtab = structureStore.currentEtab
      ? structureStore.currentEtab.filieres
      : [];

    return filieres.value?.filter((filiere) =>
      filieresInEtab.includes(filiere.id)
    );
  });

  return { filieres, init, currentEtabFilieres };
});