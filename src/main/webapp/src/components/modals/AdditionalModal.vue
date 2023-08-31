<script setup lang="ts">
import CheckboxLayout from "@/components/layouts/CheckboxLayout.vue";
import BaseModal from "@/components/modals/BaseModal.vue";
import PersonneSearch from "@/components/search/PersonneSearch.vue";
import { setPersonneAdditional } from "@/services/personneService";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useFonctionStore } from "@/stores/fonctionStore";
import { usePersonneStore } from "@/stores/personneStore";
import { Tabs } from "@/types/enums/Tabs";
import type { SimplePersonne } from "@/types/personneType";
import { errorHandler } from "@/utils/axiosUtils";
import debounce from "lodash.debounce";
import { storeToRefs } from "pinia";
import { watch, computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useToast } from "vue-toastification";

const { t } = useI18n();
const toast = useToast();

const configurationStore = useConfigurationStore();
const {
  currentTab,
  currentStructureId,
  isAdditional,
  // isAddMode
} = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { customMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { initCurrentPersonne, refreshCurrentPersonne } = personneStore;
const {
  currentPersonne,
  structureFonctions,
  hasStructureFonctions,
  structureAdditionalFonctions,
  hasStructureAdditionalFonctions,
  administrativeList,
  teachingList,
} = storeToRefs(personneStore);

const setSelectedUser = (id: number | undefined) => {
  currentPersonne.value = undefined;
  if (id) {
    // isAddMode.value = true;
    initCurrentPersonne(id, false);
  }
};

watch(currentPersonne, (newValue) => {
  if (newValue) {
    selected.value = structureAdditionalFonctions.value
      ? structureAdditionalFonctions.value?.map(
          (fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`
        )
      : [];
  }
});

const selected = ref<Array<string>>([]);

const setSelected = (value: Array<string>) => {
  selected.value = value;
};

const canSave = computed<boolean>(() => {
  if (selected.value.length == structureAdditionalFonctions.value?.length) {
    return !selected.value.every(
      (entry) =>
        structureAdditionalFonctions.value
          ?.map((fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`)
          .includes(entry)
    );
  }
  return true;
});

const saveButton = computed<{ i18n: string; icon: string; color: string }>(
  () => {
    if (!hasStructureFonctions.value) {
      if (!hasStructureAdditionalFonctions.value)
        return { i18n: "button.attach", icon: "fas fa-link", color: "success" };
      if (selected.value.length == 0)
        return {
          i18n: "button.detach",
          icon: "fas fa-link-slash",
          color: "error",
        };
    }
    return {
      i18n: "button.save",
      icon: "fas fa-floppy-disk",
      color: "success",
    };
  }
);

const save = async () => {
  try {
    await setPersonneAdditional(
      currentPersonne.value!.id,
      currentStructureId.value!,
      selected.value
    );
    closeAndResetModal(true);
  } catch (e) {
    errorHandler(e);
    closeAndResetModal(false);
  }
};

const closeAndResetModal = (success?: boolean) => {
  const { i18n } = saveButton.value;
  const title = i18n.replace("button.", "");
  if (success) {
    refreshCurrentPersonne();
    toast.success(t(`toast.additional.success.${title}`));
  } else if (!success && success != undefined) {
    toast.error(t(`toast.additional.error.${title}`));
  }

  if (isAdditional.value) isAdditional.value = false;
  const reset = debounce(() => {
    currentPersonne.value = undefined;
    selected.value = [];
  }, 200);
  reset();
};

const currentTabValue = computed<{
  title: string;
  searchList: Array<SimplePersonne> | undefined;
  filieres: Array<any> | undefined;
}>(() => {
  switch (currentTab.value) {
    case Tabs.AdministrativeStaff:
      return {
        title: t("addAdditionalFonction"),
        searchList: administrativeList.value,
        filieres: customMapping.value?.filieres,
      };
    case Tabs.TeachingStaff:
      return {
        title: t("addAdditionalTeaching"),
        searchList: teachingList.value,
        filieres: [],
      };
    default:
      return { title: "", searchList: undefined, filieres: undefined };
  }
});
</script>

<template>
  <base-modal
    v-model="isAdditional"
    :title="currentTabValue.title"
    @update:model-value="
      (value: boolean) => {
        if (!value) closeAndResetModal();
      }
    "
  >
    <personne-search
      :search-list="currentTabValue.searchList"
      @update:select="setSelectedUser"
      :class="currentPersonne ? 'mb-4' : 'mb-6'"
    />
    <div v-if="currentPersonne">
      <checkbox-layout
        :filieres="currentTabValue.filieres"
        :selected="selected"
        :disabled="
          structureFonctions?.map(
            (fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`
          )
        "
        @update:selected="setSelected"
      />
    </div>
    <template #footer v-if="currentPersonne">
      <v-btn
        :color="saveButton.color"
        :prepend-icon="saveButton.icon"
        :disabled="!canSave"
        @click="save"
      >
        {{ t(saveButton.i18n) }}
      </v-btn>
    </template>
  </base-modal>
</template>
