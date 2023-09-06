import { getConfiguration } from '@/services/configurationService';
import type { Configuration } from '@/types/configurationType';
import { Tabs } from '@/types/enums/Tabs';
import type { Identity } from '@/types/identityType';
import { errorHandler } from '@/utils/axiosUtils';
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
    try {
      const response = await getConfiguration();
      configuration.value = response.data;
    } catch (e) {
      errorHandler(e, 'initConfigurationStore');
    }
  };

  const isInit = computed<boolean>(() => configuration.value != undefined);

  /**
   * Retourne la liste des types de personnel administratif
   */
  const administrativeStaff = computed<Array<string> | undefined>(() => configuration.value?.administrativeStaff);

  /**
   * Retourne la liste des codes de filière d'administration
   */
  const administrativeCodes = computed<Array<string> | undefined>(() => configuration.value?.administrativeCodes);

  /**
   * Retourne la liste des codes de filière d'enseigments
   */
  const teachingCodes = computed<Array<string> | undefined>(() => configuration.value?.teachingCodes);

  const isExternalLogin = (categorie: string, source: string): boolean => {
    if (configuration.value) {
      return (
        configuration.value.externalSourcesAll.includes(source) ||
        (configuration.value.externalSources4Login.includes(source) &&
          configuration.value.externalSources4LoginCategory.includes(categorie))
      );
    }
    return false;
  };

  const isEditAllowed = (etat: string): boolean => {
    if (configuration.value) {
      return configuration.value.editAllowedStates.includes(etat);
    }
    return false;
  };

  /* --- Gestion des onglets de structure --- */

  const structures = ref(useStorage<Array<{ id: number; name: string }>>('tabs', [], sessionStorage));
  const currentStructure = ref<number | undefined>();
  const currentTab = ref<number>(Tabs.Dashboard);

  const setCurrentStructure = (value: number): void => {
    currentStructure.value = value;
  };

  const setCurrentTab = (value: number): void => {
    currentTab.value = value;
  };

  const currentStructureId = ref<number | undefined>();

  const setCurrentStructureId = (id: number): void => {
    currentStructureId.value = id;
  };

  /* -- Gestion de la modale des complémentaires -- */

  const isAdditional = ref<boolean>(false);
  const isAddMode = ref<boolean>(false);

  /* -- Gestion de l'authentification -- */

  const identity = ref<Identity | undefined>();
  const isAuthenticated = computed<boolean>(() => !isEmpty(identity.value));
  const isAdmin = computed<boolean>(() => {
    const hasRoleAdmin = identity.value?.roles.includes('ROLE_ADMIN');

    return hasRoleAdmin != undefined && hasRoleAdmin;
  });

  return {
    init,
    isInit,
    administrativeStaff,
    administrativeCodes,
    teachingCodes,
    isExternalLogin,
    isEditAllowed,
    structures,
    currentStructure,
    currentTab,
    setCurrentStructure,
    setCurrentTab,
    currentStructureId,
    setCurrentStructureId,
    isAdditional,
    isAddMode,
    identity,
    isAuthenticated,
    isAdmin,
  };
});
