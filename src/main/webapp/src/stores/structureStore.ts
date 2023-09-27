import { getEtablissement, getEtablissements } from '@/services/structureService';
import { useConfigurationStore } from '@/stores/configurationStore';
import { Tabs } from '@/types/enums/Tabs';
import type { Etablissement, SimpleEtablissement } from '@/types/etablissementType';
import { errorHandler } from '@/utils/axiosUtils';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export const useStructureStore = defineStore('structure', () => {
  const configurationStore = useConfigurationStore();

  const etabs = ref<Array<SimpleEtablissement> | undefined>();
  const currentEtab = ref<Etablissement | undefined>();

  /**
   * Initialise `etabs`
   */
  const init = async (): Promise<void> => {
    if (!isInit.value) {
      try {
        const response = await getEtablissements();
        etabs.value = response.data;
      } catch (e) {
        errorHandler(e, 'initStructureStore');
      }
    }
  };

  const isInit = computed<boolean>(() => (etabs.value ? etabs.value.length > 0 : false));

  /**
   * Initialise `currentEtab`
   * @param id Identifiant de la structure
   */
  const initCurrentEtab = async (id: number): Promise<void> => {
    const { structures, setCurrentStructure, setCurrentTab, setCurrentStructureId } = configurationStore;

    try {
      const response = await getEtablissement(id);
      currentEtab.value = response.data;

      // Mise à jour de l'onglet
      const index = structures.findIndex((structures) => structures.id == id);
      if (index == -1) {
        setCurrentTab(Tabs.Dashboard);
        structures.push({
          id: id,
          name: currentEtab?.value?.type
            ? `${currentEtab.value.type} ${currentEtab.value.nom}`
            : currentEtab?.value?.nom ?? '',
        });
        setCurrentStructure(structures.length - 1);
      } else setCurrentStructure(index);
      setCurrentStructureId(id);
    } catch (e) {
      errorHandler(e, 'initCurrentEtab');
    }
  };

  const refreshCurrentStructure = async (): Promise<void> => {
    const structureId = currentEtab.value?.id;
    if (structureId) {
      try {
        const response = await getEtablissement(structureId);
        currentEtab.value = response.data;
      } catch (e) {
        errorHandler(e, 'refreshCurrentStructure');
      }
    }
  };

  return {
    etabs,
    currentEtab,
    init,
    initCurrentEtab,
    refreshCurrentStructure,
  };
});
