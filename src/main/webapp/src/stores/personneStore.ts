import { getPersonne } from "@/services/personneService";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useStructureStore } from "@/stores/structureStore";
import { CategoriePersonne } from "@/types/enums/CategoriePersonne";
import { Etat } from "@/types/enums/Etat";
import type { PersonneFonction } from "@/types/fonctionType";
import type {
  Personne,
  SearchPersonne,
  SimplePersonne,
} from "@/types/personneType";
import isEmpty from "lodash.isempty";
import { defineStore } from "pinia";
import { computed, ref } from "vue";

export const usePersonneStore = defineStore("personne", () => {
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
  const initCurrentPersonne = async (
    id: number,
    showModal: boolean
  ): Promise<void> => {
    try {
      const response = await getPersonne(id);
      currentPersonne.value = response.data;
      isCurrentPersonne.value = showModal;
    } catch (e) {
      console.error(e.message);
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
        console.error(e.message);
      }
    }
  };

  const structureFonctions = computed(
    (): Array<PersonneFonction> | undefined => {
      const { currentStructureId } = configurationStore;

      return currentPersonne.value?.fonctions.filter(
        (fonction) => fonction.structure == currentStructureId
      );
    }
  );

  const hasStructureFonctions = computed(() => {
    return structureFonctions.value
      ? structureFonctions.value.length > 0
      : false;
  });

  const structureAdditionalFonctions = computed(
    (): Array<PersonneFonction> | undefined => {
      const { currentStructureId } = configurationStore;

      return currentPersonne.value?.additionalFonctions.filter(
        (fonction) => fonction.structure == currentStructureId
      );
    }
  );

  const hasStructureAdditionalFonctions = computed(() => {
    return structureAdditionalFonctions.value
      ? structureAdditionalFonctions.value.length > 0
      : false;
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

    const result = currentEtab?.personnes?.filter(
      (personne) => personne.etat == Etat.Delete
    );

    return isEmpty(result) ? [] : result!;
  });

  /**
   * Retourne la liste des personnes passés avec un display name
   */
  const toSearchList = (
    list: Array<SimplePersonne> | undefined
  ): Array<SearchPersonne> | undefined => {
    return list
      ? list.map((personne) => {
          return {
            ...personne,
            displayName: personne.patronyme
              ? `${personne.patronyme} ${personne.givenName}`
              : personne.givenName,
          };
        })
      : undefined;
  };

  /**
   * Retourne la liste des personnes de la structure courante pour la recherche
   */
  const searchList = computed<Array<SearchPersonne> | undefined>(() => {
    const { currentEtab } = structureStore;

    return toSearchList(currentEtab?.personnes);
  });

  /**
   * Retourne la liste des personnels administratifs de la structure courante
   * pour la recherche
   */
  const administrativeSearchList = computed<Array<SearchPersonne> | undefined>(
    () => {
      const { currentEtab } = structureStore;
      const { administrativeStaff } = configurationStore;

      const staff = currentEtab?.personnes.filter((personne) =>
        administrativeStaff?.includes(personne.categorie)
      );

      return toSearchList(staff);
    }
  );

  /**
   * Retourne la liste des personnels enseignants de la structure courante pour
   * la recherche
   */
  const teachingSearchList = computed<Array<SearchPersonne> | undefined>(() => {
    const { currentEtab } = structureStore;

    const staff = currentEtab?.personnes.filter(
      (personne) => (personne.categorie = CategoriePersonne.Enseignant)
    );

    return toSearchList(staff);
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
    searchList,
    administrativeSearchList,
    teachingSearchList,
  };
});
