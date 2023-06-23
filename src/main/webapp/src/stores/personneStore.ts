import { useConfigurationStore } from "./configurationStore";
import { useStructureStore } from "./structureStore";
import { getPersonne } from "@/services/personneService";
import type { Personne, SimplePersonne } from "@/types/personneType";
import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const usePersonneStore = defineStore("personne", () => {
  const configurationStore = useConfigurationStore();
  const structureStore = useStructureStore();

  const currentPersonne = ref<Personne | undefined>();

  const isCurrentPersonne = computed({
    get(): boolean {
      return currentPersonne.value ? true : false;
    },
    set(): void {
      currentPersonne.value = undefined;
    },
  });

  const personnes = computed((): Array<SimplePersonne> | undefined => {
    const { personnes } = structureStore.currentEtab;

    return personnes;
  });

  const administrative = computed((): Array<SimplePersonne> | undefined => {
    const { administrativeStaff } = configurationStore;

    return personnes.value?.filter((personne) =>
      administrativeStaff?.includes(personne.categorie)
    );
  });

  const initCurrentPersonne = async (id: number): Promise<void> => {
    currentPersonne.value = (await getPersonne(id)).data.payload;
  };

  return {
    currentPersonne,
    isCurrentPersonne,
    administrative,
    initCurrentPersonne,
  };
});
