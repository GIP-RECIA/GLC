<script setup lang="ts">
import CheckboxLayout from "@/components/layouts/CheckboxLayout.vue";
import BaseModal from "@/components/modals/BaseModal.vue";
import PersonneSearch from "@/components/search/PersonneSearch.vue";
import { getPersonne, setPersonneAdditional } from "@/services/personneService";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useFonctionStore } from "@/stores/fonctionStore";
import { usePersonneStore } from "@/stores/personneStore";
import { Tabs } from "@/types/enums/Tabs";
import type { PersonneFonction } from "@/types/fonctionType";
import type { Personne } from "@/types/personneType";
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { currentTab, isAdditional } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { customMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { administrativeSearchList, teachingSearchList } =
  storeToRefs(personneStore);

const route = useRoute();
const { structureId } = route.params;

const currentPersonne = ref<Personne | undefined>();

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

const selectedUser = ref<number | undefined>();
const selected = ref<Array<string>>([]);

const isSelected = computed<boolean>(() => selected.value.length > 0);

const setSelected = (value: Array<string>) => {
  selected.value = value;
};

const isSelectedUser = computed<boolean>(() => selectedUser.value != undefined);

const setSelectedUser = async (id: number | undefined) => {
  selectedUser.value = id;
  if (selectedUser.value) {
    currentPersonne.value = (await getPersonne(id!)).data;
  }
};

const save = () => {
  isAdditional.value = false;
  setPersonneAdditional(selectedUser.value!, selected.value);
};

const currentTabValue = () => {
  switch (currentTab.value) {
    case Tabs.AdministrativeStaff:
      return {
        title: t("addAdditionalFonction"),
        searchList: administrativeSearchList.value,
        filieres: customMapping.value?.filieres,
      };
    case Tabs.TeachingStaff:
      return {
        title: t("addAdditionalTeaching"),
        searchList: teachingSearchList.value,
        filieres: [],
      };
    default:
      return { title: "", searchList: undefined, filieres: undefined };
  }
};
</script>

<template>
  <base-modal v-model="isAdditional" :title="currentTabValue().title">
    <personne-search
      :search-list="currentTabValue().searchList"
      @update:select="setSelectedUser"
    />
    <div
      v-if="selectedUser != undefined && currentPersonne != undefined"
      class="mt-4"
    >
      <checkbox-layout
        :filieres="currentTabValue().filieres"
        :selected="
          structureAdditionalFonctions(Number(structureId))?.map(
            (fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`
          )
        "
        :disabled="
          structureFonctions(Number(structureId))?.map(
            (fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`
          )
        "
        @update:selected="setSelected"
      />
    </div>
    <template #footer>
      <v-btn
        color="success"
        prepend-icon="fas fa-floppy-disk"
        :disabled="!isSelectedUser || !isSelected"
        @click="save"
      >
        {{ t("save") }}
      </v-btn>
    </template>
  </base-modal>
</template>
