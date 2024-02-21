import { getEtablissement, getEtablissements } from '@/services/structureService.ts';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { Etat } from '@/types/enums/Etat.ts';
import { Tabs } from '@/types/enums/Tabs.ts';
import type { Etablissement, SimpleEtablissement } from '@/types/etablissementType.ts';
import type { Filiere } from '@/types/filiereType.ts';
import type { CustomMapping, SourceFonction } from '@/types/fonctionType.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import { errorHandler } from '@/utils/axiosUtils.ts';
import isEmpty from 'lodash.isempty';
import { defineStore, storeToRefs } from 'pinia';
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
    const { structures, setAppTab, setStructureTab, setCurrentStructureId } = configurationStore;
    configurationStore.isLoading = true;
    try {
      const response = await getEtablissement(id);
      const etab = response.data;

      // Mise à jour de l'onglet
      const index = structures.findIndex((structures) => structures.id == id);
      if (index == -1) {
        setStructureTab(Tabs.Dashboard);
        structures.push({
          id: id,
          name: etab.type ? `${etab.type} ${etab.nom}` : etab.nom ?? '',
          config: {
            dashboard: {},
            info: {},
            teachingStaff: {
              accountStates: [],
            },
            schoolStaff: {
              accountStates: [],
            },
            academicStaff: {
              accountStates: [],
            },
            collectivityStaff: {
              accountStates: [],
            },
            accounts: {},
          },
        });
        setAppTab(structures.length - 1);
      } else setAppTab(index);
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

  const personnes = computed<Array<SimplePersonne> | undefined>(() => currentEtab.value?.personnes);

  const staff = computed(() => {
    const { configuration } = storeToRefs(configurationStore);

    const getStaff = (categorie?: string): Array<SimplePersonne> | undefined => {
      return personnes.value?.filter((personne) => personne.categorie == categorie);
    };

    return {
      teaching: getStaff(configuration.value?.front.staff.teaching),
      school: getStaff(configuration.value?.front.staff.school),
      collectivity: getStaff(configuration.value?.front.staff.collectivity),
      academic: getStaff(configuration.value?.front.staff.academic),
    };
  });

  const personnesByEtat = computed<Map<Etat, Array<SimplePersonne> | undefined>>(() => {
    const map = new Map();

    Object.keys(Etat).forEach((etat) => {
      map.set(
        etat,
        personnes.value?.filter((personne) => personne.etat == etat),
      );
    });

    return map;
  });

  const fonction = computed<SourceFonction | undefined>(() => {
    const { fonctions } = storeToRefs(configurationStore);

    const fonction: SourceFonction | undefined = fonctions.value?.find(
      (fonction) => fonction.source === currentEtab.value?.source,
    );
    const customMapping: CustomMapping | undefined = isEmpty(fonction?.customMapping)
      ? undefined
      : fonction.customMapping;

    return fonction ? { ...fonction, customMapping } : undefined;
  });

  const filieresByStaff = computed(() => {
    const { configuration } = storeToRefs(configurationStore);

    const getFiliere = (categorie?: string): Array<Filiere> | undefined => {
      return currentEtab.value?.filieres
        .map((filiere) => {
          const disciplines = filiere.disciplines.filter((discipline) => {
            return discipline.personnes.filter((personne) => personne.categorie == categorie).length > 0;
          });

          return { ...filiere, disciplines };
        })
        .filter((filiere) => filiere.disciplines.length > 0);
    };

    return {
      teaching: getFiliere(configuration.value?.front.staff.teaching),
      school: getFiliere(configuration.value?.front.staff.school),
      collectivity: getFiliere(configuration.value?.front.staff.collectivity),
      academic: getFiliere(configuration.value?.front.staff.academic),
    };
  });

  return {
    etabs,
    currentEtab,
    init,
    initCurrentEtab,
    refreshCurrentStructure,
    personnes,
    staff,
    personnesByEtat,
    fonction,
    filieresByStaff,
  };
});
