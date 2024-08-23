/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { getConfiguration } from '@/services/configurationService.ts';
import { getFonctions } from '@/services/fonctionService.ts';
import type { Configuration } from '@/types/configurationType.ts';
import type { enumValues } from '@/types/enumValuesType.ts';
import { PersonneDialogState } from '@/types/enums/PersonneDialogState.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import type { Filiere } from '@/types/filiereType.ts';
import type { SourceFonction } from '@/types/fonctionType.ts';
import type { Identity } from '@/types/identityType.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import type { StructureConfiguration } from '@/types/structureConfigurationType.ts';
import { getEtat } from '@/utils/accountUtils.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import { useEntTheme } from '@/utils/entUtils.ts';
import { useSessionStorage } from '@vueuse/core';
import isEmpty from 'lodash.isempty';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export const useConfigurationStore = defineStore('configuration', () => {
  const configuration = ref<Configuration | undefined>();
  const fonctions = ref<Array<SourceFonction> | undefined>();

  /**
   * Initialise `configuration`
   */
  const init = async (): Promise<void> => {
    if (!isInit.value) {
      try {
        const response = await getConfiguration();
        configuration.value = response.data;
        await useEntTheme(configuration.value!.front.extendedUportalHeader.templateApiPath);
      } catch (e) {
        errorHandler(e, 'initConfigurationStore');
      }
    }
  };

  const initFonctions = async (): Promise<void> => {
    if (!isInitFonctions.value) {
      try {
        const response = await getFonctions();
        fonctions.value = response.data;
      } catch (e) {
        errorHandler(e, 'initFonctionStore');
      }
    }
  };

  const isInit = computed<boolean>(() => configuration.value != undefined);
  const isInitFonctions = computed<boolean>(() => fonctions.value != undefined);

  const allFilieres = computed<Array<Filiere> | undefined>(() => {
    return fonctions.value ? fonctions.value.find((fonction) => fonction.source === 'ALL')?.filieres : undefined;
  });

  /**
   * Retourne la liste des types de personnel administratif
   */
  const administrativeStaff = computed<string | undefined>(() => configuration.value?.front.staff.school);

  const isEditAllowed = (etat: string): boolean => {
    if (configuration.value) {
      return configuration.value.front.editAllowedStates.includes(etat);
    }
    return false;
  };

  const loginOffices = computed(() => configuration.value?.front.loginOffices);

  const getLoginOffice = (categorie: string, source: string): string | undefined => {
    if (loginOffices.value) {
      const sources = loginOffices.value.filter((office) => office.source == source);
      if (sources.length <= 0) return undefined;
      else if (sources.length > 1) throw new Error(`Can not resolve guichet for source ${source}`);

      const offices: Array<string> = sources[0].guichets
        .filter((guichet) => guichet.categoriesPersonne.includes(categorie))
        .map((guichet) => guichet.nom);
      if (offices.length > 1) throw new Error(`Can not resolve guichet for categorie ${categorie}`);

      return offices[0];
    }
    return undefined;
  };

  const filterAccountStates = computed<Array<enumValues & { value: string }> | undefined>(() =>
    configuration.value?.front.filterAccountStates?.map((state) => {
      return { ...getEtat(state), value: state };
    }),
  );

  /* --- Gestion des onglets de structure --- */

  const structures = useSessionStorage<Array<{ id: number; name: string; config: StructureConfiguration }>>(
    `${__APP_SLUG__}.tabs`,
    [],
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

  /* -- Gestion de la modale personne -- */

  const personneDialogState = ref<PersonneDialogState>(PersonneDialogState.Info);

  const isAttach = ref<boolean>(false);
  const attachMode = ref<boolean>(false);

  /* -- Gestion de la modale des complémentaires -- */

  const isQuickAdd = ref<boolean>(false);
  const requestAdd = ref<{
    i18n?: string;
    function?: string;
    type: 'id' | 'code';
    searchList?: Array<SimplePersonne>;
  }>();

  /* -- Gestion de la modale des paramètres -- */

  const isSettings = ref<boolean>(false);

  /* -- Gestion de l'authentification -- */

  const identity = ref<Identity | undefined>();
  const isAuthenticated = computed<boolean>(() => !isEmpty(identity.value));

  return {
    configuration,
    fonctions,
    init,
    initFonctions,
    isInit,
    isInitFonctions,
    allFilieres,
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
    personneDialogState,
    isAttach,
    attachMode,
    isQuickAdd,
    requestAdd,
    isSettings,
    identity,
    isAuthenticated,
  };
});
