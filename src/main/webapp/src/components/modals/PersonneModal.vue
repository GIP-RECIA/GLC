<script setup lang="ts">
import ReadonlyData from "@/components/ReadonlyData.vue";
import CheckboxLayout from "@/components/layouts/CheckboxLayout.vue";
import FonctionsLayout from "@/components/layouts/FonctionsLayout.vue";
import BaseModal from "@/components/modals/BaseModal.vue";
import { setPersonneAdditional } from "@/services/personneService";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useFonctionStore } from "@/stores/fonctionStore";
import { usePersonneStore } from "@/stores/personneStore";
import { Etat } from "@/types/enums/Etat";
import { Tabs } from "@/types/enums/Tabs";
import moment from "moment";
import { storeToRefs } from "pinia";
import { watch, ref, computed } from "vue";
import { useI18n } from "vue-i18n";
import { useRoute } from "vue-router";

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { currentTab, isAdmin } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { customMapping, isCustomMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { structureFonctions, structureAdditionalFonctions } = personneStore;
const { currentPersonne, isCurrentPersonne } = storeToRefs(personneStore);

const route = useRoute();
const { structureId } = route.params;

const isLocked = ref<boolean>(false);
const isAddMode = ref<boolean>(false);
const selected = ref<Array<string>>([]);

const isSelected = computed<boolean>(() => selected.value.length > 0);

const setSelected = (value: Array<string>) => {
  selected.value = value;
};

watch(isCurrentPersonne, (newValue) => {
  if (!newValue) {
    isAddMode.value = false;
    selected.value = [];
  } else {
    const items = structureAdditionalFonctions(Number(structureId))?.map(
      (fonction) => `${fonction.filiere}-${fonction.disciplinePoste}`
    );
    selected.value = items ? items : [];
    isLocked.value = currentPersonne.value!.etat == Etat.Bloque;
  }
});

const lockManager = () => {
  isLocked.value = !isLocked.value;
};

const reinitialize = () => {};

const save = () => {
  isAddMode.value = false;
  if (currentPersonne.value) {
    setPersonneAdditional(currentPersonne.value.id, selected.value);
  }
};

const cancel = () => {
  isAddMode.value = false;
};
</script>

<template>
  <base-modal
    v-model="isCurrentPersonne"
    :title="
      currentPersonne
        ? currentPersonne.patronyme
          ? `${currentPersonne.patronyme} ${currentPersonne.givenName}`
          : currentPersonne.givenName
        : ''
    "
  >
    <div v-if="currentPersonne && !isAddMode">
      <div class="d-flex flex-row flex-wrap">
        <readonly-data
          v-if="isAdmin"
          label="uid"
          :value="currentPersonne.uid"
          class="modal-flex-item"
        />
        <readonly-data
          v-if="isAdmin"
          label="uuid"
          :value="currentPersonne.uuid"
          class="modal-flex-item"
        />
        <readonly-data
          label="idEduConnect"
          :value="currentPersonne.idEduConnect"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('civility')"
          :value="currentPersonne.civilite"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('lastName')"
          :value="currentPersonne.patronyme"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('firstName')"
          :value="currentPersonne.givenName"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('birthDate')"
          :value="moment(currentPersonne.dateNaissance).format('L')"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('email') + ' ac'"
          :value="currentPersonne.email"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('email') + ' perso'"
          :value="currentPersonne.emailPersonnel"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('schoolYear')"
          :value="moment(currentPersonne.anneeScolaire).format('Y')"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('login')"
          :value="currentPersonne.login"
          class="modal-flex-item"
        />
        <readonly-data
          :label="t('status')"
          :value="currentPersonne.etat"
          class="modal-flex-item"
        />
        <readonly-data
          label="categorie"
          :value="currentPersonne.categorie"
          class="modal-flex-item"
        />
        <readonly-data
          label="cn"
          :value="currentPersonne.cn"
          class="modal-flex-item"
        />
        <readonly-data
          label="displayName"
          :value="currentPersonne.displayName"
          class="modal-flex-item"
        />
        <readonly-data
          label="numBureau"
          :value="currentPersonne.numBureau"
          class="modal-flex-item"
        />
        <readonly-data
          label="sn"
          :value="currentPersonne.sn"
          class="modal-flex-item"
        />
        <readonly-data
          label="titre"
          :value="currentPersonne.titre"
          class="modal-flex-item"
        />
        <readonly-data
          label="listeRouge"
          :value="currentPersonne.listeRouge.toString()"
          class="modal-flex-item"
        />
        <readonly-data
          label="forceEtat"
          :value="currentPersonne.forceEtat"
          class="modal-flex-item"
        />
        <readonly-data
          label="source"
          :value="currentPersonne.source"
          class="modal-flex-item"
        />
      </div>
      <div class="mb-3">
        <b>{{ t("function", 2) }}</b>
        <fonctions-layout
          :fonctions="structureFonctions(Number(structureId))!"
          class="mt-2"
        />
      </div>
      <div>
        <b>{{ t("additionalFunction", 2) }}</b>
        <fonctions-layout
          :fonctions="structureAdditionalFonctions(Number(structureId))!"
          class="mt-2"
        />
      </div>
    </div>

    <div v-if="isAddMode">
      <checkbox-layout
        v-if="currentTab == Tabs.AdministrativeStaff"
        :filieres="customMapping?.filieres ? customMapping.filieres : []"
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
      <div class="d-flex justify-space-between w-100">
        <div>
          <div v-if="!isAddMode">
            <v-btn
              v-if="
                currentPersonne?.etat == Etat.Bloque ||
                currentPersonne?.etat == Etat.Valide
              "
              color="secondary"
              :prepend-icon="isLocked ? 'fas fa-lock-open' : 'fas fa-lock'"
              @click="lockManager"
            >
              {{ isLocked ? t("unlock") : t("lock") }}
            </v-btn>
            <v-btn
              color="secondary"
              prepend-icon="fas fa-rotate-right"
              @click="reinitialize"
            >
              {{ t("reinitialize") }}
            </v-btn>
          </div>
        </div>

        <div
          v-if="
            currentTab == Tabs.AdministrativeStaff ||
            currentTab == Tabs.TeachingStaff
          "
        >
          <v-btn
            v-if="!isAddMode && isCustomMapping"
            color="primary"
            prepend-icon="fas fa-plus"
            @click="isAddMode = true"
          >
            {{ t("add") }}
          </v-btn>
          <div v-if="isAddMode">
            <v-btn
              color="secondary"
              prepend-icon="fas fa-xmark"
              @click="cancel"
            >
              {{ t("cancel") }}
            </v-btn>
            <v-btn
              color="success"
              prepend-icon="fas fa-floppy-disk"
              :disabled="!isSelected"
              @click="save"
            >
              {{ t("save") }}
            </v-btn>
          </div>
        </div>
      </div>
    </template>
  </base-modal>
</template>
