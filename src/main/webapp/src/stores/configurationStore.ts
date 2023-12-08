import { getConfiguration } from '@/services/configurationService.ts';
import type { Configuration } from '@/types/configurationType.ts';
import type { enumValues } from '@/types/enumValuesType.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import type { Identity } from '@/types/identityType.ts';
import type { StructureConfiguration } from '@/types/structureConfigurationType.ts';
import { getEtat } from '@/utils/accountUtils.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import { useStorage } from '@vueuse/core';
import isEmpty from 'lodash.isempty';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export const useConfigurationStore = defineStore('configuration', () => {
  const configuration = ref<Configuration | undefined>();

  /**
   * Initialise `configuration`
   */
  const init = async (): Promise<void> => {
    if (!isInit.value) {
      try {
        const response = await getConfiguration();
        configuration.value = response.data;
      } catch (e) {
        errorHandler(e, 'initConfigurationStore');
      }
    }
  };

  const isInit = computed<boolean>(() => configuration.value != undefined);

  /**
   * Retourne la liste des types de personnel administratif
   */
  const administrativeStaff = computed<Array<string> | undefined>(() => configuration.value?.administrativeStaff);

  const isEditAllowed = (etat: string): boolean => {
    if (configuration.value) {
      return configuration.value.editAllowedStates.includes(etat);
    }
    return false;
  };

  const loginOffices = computed(() => configuration.value?.loginOffices);

  const getLoginOffice = (categorie: string, source: string): string | undefined => {
    let guichet = undefined;

    if (loginOffices.value) {
      if (!loginOffices.value.map((office) => office.source).includes(source)) return undefined;

      loginOffices.value.forEach((office) => {
        if (office.source == source) {
          const guichets: Array<string> = office.guichets
            .filter((guichet) => guichet.categoriesPersonne.includes(categorie))
            .map((guichet) => guichet.nom);

          if (guichets.length > 1) throw new Error(`Can not resolve guichet for ${categorie} ${source}`);

          guichet = guichets[0];
        }
      });
    }

    return guichet;
  };

  const filterAccountStates = computed<Array<enumValues & { value: string }> | undefined>(
    () =>
      configuration.value?.filterAccountStates?.map((state) => {
        return { ...getEtat(state), value: state };
      }),
  );

  /* --- Gestion des onglets de structure --- */

  const structures = ref(
    useStorage<Array<{ id: number; name: string; config: StructureConfiguration }>>('tabs', [], sessionStorage),
  );
  const appTab = ref<number | undefined>();

  const setAppTab = (value: number): void => {
    appTab.value = value;
  };

  const currentStructureId = ref<number | undefined>();

  const setCurrentStructureId = (id: number): void => {
    currentStructureId.value = id;
  };

  const currentStructureConfig = computed<StructureConfiguration | undefined>({
    get() {
      const index = structures.value.findIndex((structure) => structure.id == currentStructureId.value);
      return index >= 0 ? structures.value[index].config : undefined;
    },
    set(configuration) {
      if (configuration != undefined) {
        const index = structures.value.findIndex((structure) => structure.id == currentStructureId.value);
        if (index >= 0) {
          structures.value[index].config = configuration;
        }
      }
    },
  });

  /* --- Gestion de la structure courrante --- */

  const structureTab = ref<number>(Tabs.Dashboard);

  const setStructureTab = (value: number): void => {
    structureTab.value = value;
  };

  /* --- Gestion de la home --- */

  const search = ref<string | undefined>();

  /* --- Gestion du spinner --- */

  const isLoading = ref<boolean>(false);

  /* -- Gestion de la modale des complémentaires -- */

  const isAdditional = ref<boolean>(false);
  const isAddMode = ref<boolean>(false);

  /* -- Gestion de l'authentification -- */

  const identity = ref<Identity | undefined>();
  const isAuthenticated = computed<boolean>(() => !isEmpty(identity.value));

  return {
    init,
    isInit,
    administrativeStaff,
    isEditAllowed,
    getLoginOffice,
    filterAccountStates,
    structures,
    appTab,
    setAppTab,
    currentStructureId,
    setCurrentStructureId,
    currentStructureConfig,
    structureTab,
    setStructureTab,
    search,
    isLoading,
    isAdditional,
    isAddMode,
    identity,
    isAuthenticated,
  };
});
