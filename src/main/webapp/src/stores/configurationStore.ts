import { getConfiguration } from "@/services/configurationService";
import type { Configuration } from "@/types/configurationType";
import { Tabs } from "@/types/enums/Tabs";
import type { Identity } from "@/types/identityType";
import isEmpty from "lodash.isempty";
import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const useConfigurationStore = defineStore("configuration", () => {
  const configuration = ref<Configuration | undefined>();

  /**
   * Initialise `configuration`
   */
  const init = async (): Promise<void> => {
    try {
      const response = await getConfiguration();
      configuration.value = response.data;
    } catch (e) {
      console.error(e.message);
    }
  };

  const casUrlLogin = computed<string | undefined>(
    () => configuration.value?.casUrlLogin
  );

  const casUrlLogout = computed<string | undefined>(
    () => configuration.value?.casUrlLogout
  );

  /**
   * Retourne la liste des types de personnel administratif
   */
  const administrativeStaff = computed<Array<string> | undefined>(
    () => configuration.value?.administrativeStaff
  );

  /**
   * Retourne la liste des codes de filière d'administration
   */
  const administrativeCodes = computed<Array<string> | undefined>(
    () => configuration.value?.administrativeCodes
  );

  /**
   * Retourne la liste des codes de filière d'enseigments
   */
  const teachingCodes = computed<Array<string> | undefined>(
    () => configuration.value?.teachingCodes
  );

  /* --- Gestion des onglets de structure --- */

  const structures = ref<Array<{ id: number; name: string }>>([]);
  const currentStructure = ref<number | undefined>();
  const currentTab = ref<number>(Tabs.Dashboard);

  const setCurrentStructure = (value: number): void => {
    currentStructure.value = value;
  };

  const setCurrentTab = (value: number): void => {
    currentTab.value = value;
  };

  /* -- Gestion de la modale des complémentaires -- */

  const isAdditional = ref<boolean>(false);

  /* -- Gestion de l'authentification -- */

  const identity = ref<Identity | undefined>();
  const isAuthenticated = computed<boolean>(() => !isEmpty(identity.value));
  const isAdmin = computed<boolean>(() => {
    const hasRoleAdmin = identity.value?.roles.includes("ROLE_ADMIN");

    return hasRoleAdmin != undefined && hasRoleAdmin;
  });

  return {
    init,
    casUrlLogin,
    casUrlLogout,
    administrativeStaff,
    administrativeCodes,
    teachingCodes,
    structures,
    currentStructure,
    currentTab,
    setCurrentStructure,
    setCurrentTab,
    isAdditional,
    identity,
    isAuthenticated,
    isAdmin,
  };
});
