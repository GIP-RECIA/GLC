import { useConfigurationStore } from './configurationStore';
import { useStructureStore } from './structureStore';
import { getFonctions } from '@/services/fonctionService';
import type { Filiere } from '@/types/filiereType';
import type { CustomMapping, SourceFonction } from '@/types/fonctionType';
import { errorHandler } from '@/utils/axiosUtils';
import isEmpty from 'lodash.isempty';
import { defineStore } from 'pinia';
import { computed, ref } from 'vue';

export const useFonctionStore = defineStore('fonctions', () => {
  const configurationStore = useConfigurationStore();
  const structureStore = useStructureStore();

  const fonctions = ref<Array<SourceFonction> | undefined>();

  /**
   * Initialise `fonction` avec la liste des fonctions et disciplines
   * par source
   */
  const init = async (): Promise<void> => {
    if (!isInit.value) {
      try {
        const response = await getFonctions();
        fonctions.value = response.data;
      } catch (e) {
        errorHandler(e, 'initFonctionStore');
      }
    }
  };

  const isInit = computed<boolean>(() => (fonctions.value ? fonctions.value.length > 0 : false));

  /* -- Pour la structure courante -- */

  /**
   * Retourne les filières de la structure courante
   */
  const filieres = computed<Array<Filiere> | undefined>(() => {
    const { currentEtab } = structureStore;

    return fonctions.value
      ? fonctions.value.find((fonction) => fonction.source === currentEtab?.source)?.filieres
      : undefined;
  });

  /**
   * Retourne le custom mapping de la structure courante
   */
  const customMapping = computed<CustomMapping | undefined>(() => {
    const { currentEtab } = structureStore;

    const customMapping = fonctions.value?.find((fonction) => fonction.source === currentEtab?.source)?.customMapping;

    return isEmpty(customMapping) ? undefined : customMapping;
  });

  const isCustomMapping = computed<boolean>(() => customMapping.value != undefined);

  /**
   * Retourne les filières administratives avec disciline et personnes
   * de la structure courante
   */
  const administrative = computed<Array<Filiere> | undefined>(() => {
    const { administrativeCodes } = configurationStore;
    const { currentEtab } = structureStore;

    return currentEtab?.filieres.filter((filiere) => administrativeCodes?.includes(filiere.codeFiliere));
  });

  /**
   * Retourne les filières d'enseignement avec disciplines et personnes
   * de la structure courante
   */
  const teaching = computed<Array<Filiere> | undefined>(() => {
    const { teachingCodes } = configurationStore;
    const { currentEtab } = structureStore;

    return currentEtab?.filieres.filter((filiere) => teachingCodes?.includes(filiere.codeFiliere));
  });

  return {
    init,
    isInit,
    filieres,
    customMapping,
    isCustomMapping,
    administrative,
    teaching,
  };
});
