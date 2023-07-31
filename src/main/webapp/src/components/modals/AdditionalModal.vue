<script setup lang="ts">
import CheckboxLayout from "@/components/layouts/CheckboxLayout.vue";
import BaseModal from "@/components/modals/BaseModal.vue";
import PersonneSearch from "@/components/search/PersonneSearch.vue";
import { setPersonneAdditional } from "@/services/personneService";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useFonctionStore } from "@/stores/fonctionStore";
import { usePersonneStore } from "@/stores/personneStore";
import { Tabs } from "@/types/enums/Tabs";
import debounce from "lodash.debounce";
import { storeToRefs } from "pinia";
import { watch, computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "vue-toastification";

const { t } = useI18n();
const toast = useToast();

const configurationStore = useConfigurationStore();
const { currentTab, isAdditional, currentStructureId, isAddMode } =
  storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { customMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne, refreshCurrentPersonne } = personneStore;
const {
  currentPersonne,
  isCurrentPersonne,
  structureFonctions,
  hasStructureFonctions,
  structureAdditionalFonctions,
  hasStructureAdditionalFonctions,
  administrativeSearchList,
  teachingSearchList,
} = storeToRefs(personneStore);

const selectedUser = ref<number | undefined>();
const isSelectedUser = computed<boolean>(() => selectedUser.value != undefined);

const setSelectedUser = (id: number | undefined) => {
  selectedUser.value = id;
  currentPersonne.value = undefined;
  if (id && selectedUser.value) {
    // isAddMode.value = true;
    initCurrentPersonne(id, false);
  }
};

watch(currentPersonne, (newValue) => {
  if (newValue) {
    selected.value = structureAdditionalFonctions.value?.map(
      (fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`
    );
  }
});

const selected = ref<Array<string> | undefined>([]);

const canSave = computed(() => {
  if (selected.value?.length == structureAdditionalFonctions.value?.length) {
    return !selected.value?.every((entry) =>
      structureAdditionalFonctions.value
        ?.map((fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`)
        .includes(entry)
    );
  }
  return true;
});

const setSelected = (value: Array<string>) => {
  selected.value = value;
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

const save = async () => {
  try {
    await setPersonneAdditional(
      currentPersonne.value!.id,
      currentStructureId.value!,
      selected.value!
    );
    closeAndResetModal(true);
  } catch (e) {
    console.error(e.message);
    closeAndResetModal(false);
  }
};

const closeAndResetModal = (success?: boolean) => {
  if (success) {
    refreshCurrentPersonne();
    toast.success(t("toast.additional.success", selected.value!.length));
  } else if (!success && success != undefined) {
    toast.error(t("toast.additional.error", selected.value!.length));
  }

  if (isAdditional.value) isAdditional.value = false;
  const reset = debounce(() => {
    currentPersonne.value = undefined;
    selectedUser.value = undefined;
    selected.value = [];
  }, 500);
  reset();
};
</script>

<template>
  <base-modal
    v-model="isAdditional"
    :title="currentTabValue().title"
    @update:model-value="(value: boolean) => { if (!value) closeAndResetModal();}"
  >
    <personne-search
      :search-list="currentTabValue().searchList"
      @update:select="setSelectedUser"
    />
    <div v-if="currentPersonne">
      <checkbox-layout
        :filieres="currentTabValue().filieres"
        :selected="selected"
        :disabled="
          structureFonctions?.map(
            (fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`
          )
        "
        @update:selected="setSelected"
      />
    </div>
    <template #footer>
      <v-btn
        v-if="selectedUser"
        :color="
          !hasStructureFonctions && !hasStructureAdditionalFonctions
            ? selected?.length == 0
              ? 'error'
              : 'success'
            : selected?.length == 0
            ? 'error'
            : 'success'
        "
        :prepend-icon="
          !hasStructureFonctions && !hasStructureAdditionalFonctions
            ? selected?.length == 0
              ? 'fas fa-link-slash'
              : 'fas fa-link'
            : selected?.length == 0
            ? 'fas fa-link-slash'
            : 'fas fa-floppy-disk'
        "
        :disabled="!isSelectedUser || !canSave"
        @click="save"
      >
        {{
          t(
            !hasStructureFonctions && !hasStructureAdditionalFonctions
              ? selected?.length == 0
                ? "detach"
                : "attach"
              : selected?.length == 0
              ? "detach"
              : "save"
          )
        }}
      </v-btn>
    </template>
  </base-modal>
</template>
