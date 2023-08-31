import { getPersonne } from '@/services/personneService';
import { useConfigurationStore } from '@/stores/configurationStore';
import { useStructureStore } from '@/stores/structureStore';
import { CategoriePersonne } from '@/types/enums/CategoriePersonne';
import { Etat } from '@/types/enums/Etat';
import type { PersonneFonction } from '@/types/fonctionType';
import type { Personne, SimplePersonne } from '@/types/personneType';
import { errorHandler } from '@/utils/axiosUtils';
import isEmpty from 'lodash.isempty';
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
    try {
      const response = await getPersonne(id);
      currentPersonne.value = response.data;
      isCurrentPersonne.value = showModal;
    } catch (e) {
      errorHandler(e, 'initCurrentPersonne');
    }
  };

  const refreshCurrentPersonne = async (): Promise<void> => {
    const personneId = currentPersonne.value?.id;
    if (personneId) {
      structureStore.refreshCurrentStructure();
      try {
        const response = await getPersonne(personneId);
        currentPersonne.value = response.data;
      } catch (e) {
        errorHandler(e, 'refreshCurrentPersonne');
      }
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

  const deletedPersonnes = computed<Array<SimplePersonne>>(() => {
    const { currentEtab } = structureStore;

    const result = currentEtab?.personnes?.filter((personne) => personne.etat == Etat.Delete);

    return isEmpty(result) ? [] : result!;
  });

  /**
   * Retourne la liste des personnels administratifs de la structure courante
   * pour la recherche
   */
  const administrativeList = computed<Array<SimplePersonne> | undefined>(() => {
    const { currentEtab } = structureStore;
    const { administrativeStaff } = configurationStore;

    return currentEtab?.personnes.filter((personne) => administrativeStaff?.includes(personne.categorie));
  });

  /**
   * Retourne la liste des personnels enseignants de la structure courante pour
   * la recherche
   */
  const teachingList = computed<Array<SimplePersonne> | undefined>(() => {
    const { currentEtab } = structureStore;

    return currentEtab?.personnes.filter((personne) => (personne.categorie = CategoriePersonne.Enseignant));
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
    deletedPersonnes,
    administrativeList,
    teachingList,
  };
});
