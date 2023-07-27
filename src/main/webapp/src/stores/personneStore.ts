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

  /**
   * Initialise `currentPersonne`
   *
   * @param id Identifiant de la personne
   */
  const initCurrentPersonne = async (id: number): Promise<void> => {
    try {
      const response = await getPersonne(id);
      currentPersonne.value = response.data;
    } catch (e) {
      console.error(e.message);
    }
  };

  /**
   * Retourne s'il y a une personne de défini ou l'efface
   */
  const isCurrentPersonne = computed<boolean>({
    get() {
      return currentPersonne.value ? true : false;
    },
    set() {
      currentPersonne.value = undefined;
    },
  });

  const structureFonctions = (
    structureId: number
  ): Array<PersonneFonction> | undefined => {
    return currentPersonne.value?.fonctions.filter(
      (fonction) => fonction.structure == structureId
    );
  };

  const structureAdditionalFonctions = (
    structureId: number
  ): Array<PersonneFonction> | undefined => {
    return currentPersonne.value?.additionalFonctions.filter(
      (fonction) => fonction.structure == structureId
    );
  };

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
    isCurrentPersonne,
    structureFonctions,
    structureAdditionalFonctions,
    personnes,
    deletedPersonnes,
    searchList,
    administrativeSearchList,
    teachingSearchList,
  };
});
