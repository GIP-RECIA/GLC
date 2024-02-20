import { getPersonne } from '@/services/personneService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { useStructureStore } from '@/stores/structureStore.ts';
import { Etat } from '@/types/enums/Etat.ts';
import type { PersonneFonction } from '@/types/fonctionType.ts';
import type { Personne, SimplePersonne } from '@/types/personneType.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import isEmpty from 'lodash.isempty';
import { defineStore, storeToRefs } from 'pinia';
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

  const structureFonctions = computed((): Array<PersonneFonction> | undefined => {
    const { currentStructureId } = configurationStore;

    return currentPersonne.value?.fonctions?.filter((fonction) => fonction.structure == currentStructureId);
  });

  const hasStructureFonctions = computed(() => {
    return structureFonctions.value ? structureFonctions.value.length > 0 : false;
  });

  const structureAdditionalFonctions = computed((): Array<PersonneFonction> | undefined => {
    const { currentStructureId } = configurationStore;

    return currentPersonne.value?.additionalFonctions?.filter((fonction) => fonction.structure == currentStructureId);
  });

  const hasStructureAdditionalFonctions = computed(() => {
    return structureAdditionalFonctions.value ? structureAdditionalFonctions.value.length > 0 : false;
  });

  /* -- Pour la structure courante -- */

  /**
   * Retourne la liste des personnes de la structure
   */
  const personnes = computed<Array<SimplePersonne> | undefined>(() => {
    const { currentEtab } = structureStore;

    return currentEtab?.personnes;
  });

  const deletingPersonnes = computed<Array<SimplePersonne>>(() => {
    const { currentEtab } = structureStore;

    const result = currentEtab?.personnes?.filter((personne) => personne.etat == Etat.Deleting);

    return isEmpty(result) ? [] : result!;
  });

  const deletedPersonnes = computed<Array<SimplePersonne>>(() => {
    const { currentEtab } = structureStore;

    const result = currentEtab?.personnes?.filter((personne) => personne.etat == Etat.Delete);

    return isEmpty(result) ? [] : result!;
  });

  const teachingStaffList = computed<Array<SimplePersonne> | undefined>(() => {
    const { currentEtab } = storeToRefs(structureStore);
    const { configuration } = storeToRefs(configurationStore);

    return currentEtab.value?.personnes.filter(
      (personne) => personne.categorie == configuration.value?.front.staff.teaching,
    );
  });

  const schoolStaffList = computed<Array<SimplePersonne> | undefined>(() => {
    const { currentEtab } = storeToRefs(structureStore);
    const { configuration } = storeToRefs(configurationStore);

    return currentEtab.value?.personnes.filter(
      (personne) => personne.categorie == configuration.value?.front.staff.school,
    );
  });

  const collectivityStaffList = computed<Array<SimplePersonne> | undefined>(() => {
    const { currentEtab } = storeToRefs(structureStore);
    const { configuration } = storeToRefs(configurationStore);

    return currentEtab.value?.personnes.filter(
      (personne) => personne.categorie == configuration.value?.front.staff.collectivity,
    );
  });

  const academicStaffList = computed<Array<SimplePersonne> | undefined>(() => {
    const { currentEtab } = storeToRefs(structureStore);
    const { configuration } = storeToRefs(configurationStore);

    return currentEtab.value?.personnes.filter(
      (personne) => personne.categorie == configuration.value?.front.staff.academic,
    );
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
    personnes,
    deletingPersonnes,
    deletedPersonnes,
    teachingStaffList,
    schoolStaffList,
    collectivityStaffList,
    academicStaffList,
  };
});
