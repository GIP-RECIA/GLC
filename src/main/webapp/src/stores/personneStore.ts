import { getPersonne } from '@/services/personneService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import type { PersonneFonction } from '@/types/fonctionType.ts';
import type { Personne } from '@/types/personneType.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export const usePersonneStore = defineStore('personne', () => {
  const configurationStore = useConfigurationStore();
  const structureStore = useStructureStore();

  /* -- Pour la personne courante -- */

  const currentPersonne = ref<Personne | undefined>();

  const isCurrentPersonne = ref<boolean>(false);

  /**
   * Initialise `currentPersonne`
   *
   * @param id Identifiant de la personne
   */
  const initCurrentPersonne = async (id: number, showModal: boolean): Promise<void> => {
    configurationStore.isLoading = true;
    try {
      const response = await getPersonne(id);
      currentPersonne.value = response.data;
      isCurrentPersonne.value = showModal;
    } catch (e) {
      errorHandler(e, 'initCurrentPersonne');
    }
    configurationStore.isLoading = false;
  };

  const refreshCurrentPersonne = async (): Promise<void> => {
    const personneId = currentPersonne.value?.id;
    if (personneId) {
      configurationStore.isLoading = true;
      structureStore.refreshCurrentStructure();
      try {
        const response = await getPersonne(personneId);
        currentPersonne.value = response.data;
      } catch (e) {
        errorHandler(e, 'refreshCurrentPersonne');
      }
      configurationStore.isLoading = false;
    }
  };

  const structureFonctions = computed<Array<PersonneFonction> | undefined>(() => {
    const { currentStructureId } = configurationStore;

    return currentPersonne.value?.fonctions?.filter((fonction) => fonction.structure == currentStructureId);
  });

  const hasStructureFonctions = computed<boolean>(() => {
    return structureFonctions.value ? structureFonctions.value.length > 0 : false;
  });

  const structureAdditionalFonctions = computed<Array<PersonneFonction> | undefined>(() => {
    const { currentStructureId } = configurationStore;

    return currentPersonne.value?.additionalFonctions?.filter((fonction) => fonction.structure == currentStructureId);
  });

  const hasStructureAdditionalFonctions = computed<boolean>(() => {
    return structureAdditionalFonctions.value ? structureAdditionalFonctions.value.length > 0 : false;
  });

  return {
    currentPersonne,
    initCurrentPersonne,
    refreshCurrentPersonne,
    isCurrentPersonne,
    structureFonctions,
    hasStructureFonctions,
    structureAdditionalFonctions,
    hasStructureAdditionalFonctions,
  };
});
