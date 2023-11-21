import { getEtablissement, getEtablissements } from '@/services/structureService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { Etat } from '@/types/enums/Etat.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import type { Etablissement, SimpleEtablissement } from '@/types/etablissementType.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';
import { useRouter } from 'vue-router';

export const useStructureStore = defineStore('structure', () => {
  const configurationStore = useConfigurationStore();

  const router = useRouter();

  const etabs = ref<Array<SimpleEtablissement> | undefined>();
  const currentEtab = ref<Etablissement | undefined>();

  /**
   * Initialise `etabs`
   */
  const init = async (): Promise<void> => {
    if (!isInit.value) {
      configurationStore.isLoading = true;
      try {
        const response = await getEtablissements();
        etabs.value = response.data;
      } catch (e) {
        errorHandler(e, 'initStructureStore');
      }
      configurationStore.isLoading = false;
    }
  };

  const isInit = computed<boolean>(() => (etabs.value ? etabs.value.length > 0 : false));

  /**
   * Initialise `currentEtab`
   * @param id Identifiant de la structure
   */
  const initCurrentEtab = async (id: number): Promise<void> => {
    const { structures, setCurrentStructure, setCurrentTab, setCurrentStructureId } = configurationStore;
    configurationStore.isLoading = true;
    try {
      const response = await getEtablissement(id);
      const etab = response.data;

      // Mise à jour de l'onglet
      const index = structures.findIndex((structures) => structures.id == id);
      if (index == -1) {
        setCurrentTab(Tabs.Dashboard);
        structures.push({
          id: id,
          name: etab.type ? `${etab.type} ${etab.nom}` : etab.nom ?? '',
          config: {
            administrativeStaff: {
              accountStates: [Etat.Valide],
            },
            teachingStaff: {
              accountStates: [Etat.Valide],
            },
            accounts: {},
          },
        });
        setCurrentStructure(structures.length - 1);
      } else setCurrentStructure(index);
      currentEtab.value = etab;
      setCurrentStructureId(id);
    } catch (e) {
      errorHandler(e, 'initCurrentEtab');
      router.replace({ name: 'home' });
    }
    configurationStore.isLoading = false;
  };

  const refreshCurrentStructure = async (): Promise<void> => {
    const structureId = currentEtab.value?.id;
    if (structureId) {
      configurationStore.isLoading = true;
      try {
        const response = await getEtablissement(structureId);
        currentEtab.value = response.data;
      } catch (e) {
        errorHandler(e, 'refreshCurrentStructure');
      }
      configurationStore.isLoading = false;
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
