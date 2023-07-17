<script setup lang="ts">
import ReadonlyData from "@/components/ReadonlyData.vue";
import BaseModal from "@/components/modal/BaseModal.vue";
import { setPersonneAdditionalFonctions } from "@/services/personneService";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useFonctionStore } from "@/stores/fonctionStore";
import { usePersonneStore } from "@/stores/personneStore";
import { Etat } from "@/types/enums/Etat";
import { Tabs } from "@/types/enums/Tabs";
import moment from "moment";
import { storeToRefs } from "pinia";
import { watch, ref, computed } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { currentTab } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { customMapping, isCustomMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { currentPersonne, isCurrentPersonne, additionalFonctionsForCheckboxes } =
  storeToRefs(personneStore);

const isLocked = ref<boolean>(false);
const isAddMode = ref<boolean>(false);
const selected = ref<Array<string>>([]);

const isSelected = computed<boolean>(() => selected.value.length > 0);

watch(isCurrentPersonne, (newValue) => {
  if (!newValue) {
    isAddMode.value = false;
    selected.value = [];
  } else {
    selected.value = additionalFonctionsForCheckboxes.value;
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
    setPersonneAdditionalFonctions(currentPersonne.value.id, selected.value);
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
          label="uid"
          :value="currentPersonne.uid"
          class="flex-item"
        />
        <readonly-data
          label="uuid"
          :value="currentPersonne.uuid"
          class="flex-item"
        />
        <readonly-data
          label="idEduConnect"
          :value="currentPersonne.idEduConnect"
          class="flex-item"
        />
        <readonly-data
          :label="t('civility')"
          :value="currentPersonne.civilite"
          class="flex-item"
        />
        <readonly-data
          :label="t('lastName')"
          :value="currentPersonne.patronyme"
          class="flex-item"
        />
        <readonly-data
          :label="t('firstName')"
          :value="currentPersonne.givenName"
          class="flex-item"
        />
        <readonly-data
          :label="t('birthDate')"
          :value="moment(currentPersonne.dateNaissance).format('L')"
          class="flex-item"
        />
        <readonly-data
          :label="t('email') + ' ac'"
          :value="currentPersonne.email"
          class="flex-item"
        />
        <readonly-data
          :label="t('email') + ' perso'"
          :value="currentPersonne.emailPersonnel"
          class="flex-item"
        />
        <readonly-data
          :label="t('schoolYear')"
          :value="moment(currentPersonne.anneeScolaire).format('Y')"
          class="flex-item"
        />
        <readonly-data
          :label="t('login')"
          :value="currentPersonne.login"
          class="flex-item"
        />
        <readonly-data
          :label="t('status')"
          :value="currentPersonne.etat"
          class="flex-item"
        />
        <readonly-data
          label="categorie"
          :value="currentPersonne.categorie"
          class="flex-item"
        />
        <readonly-data
          label="cn"
          :value="currentPersonne.cn"
          class="flex-item"
        />
        <readonly-data
          label="displayName"
          :value="currentPersonne.displayName"
          class="flex-item"
        />
        <readonly-data
          label="numBureau"
          :value="currentPersonne.numBureau"
          class="flex-item"
        />
        <readonly-data
          label="sn"
          :value="currentPersonne.sn"
          class="flex-item"
        />
        <readonly-data
          label="titre"
          :value="currentPersonne.titre"
          class="flex-item"
        />
        <readonly-data
          label="listeRouge"
          :value="currentPersonne.listeRouge.toString()"
          class="flex-item"
        />
        <readonly-data
          label="forceEtat"
          :value="currentPersonne.forceEtat"
          class="flex-item"
        />
      </div>
      <div class="mb-3">
        <b>{{ t("function", 2) }}</b>
        <br />
        <div
          v-for="(fonction, index) in currentPersonne.fonctions"
          :key="index"
        >
          {{ fonction }}
        </div>
      </div>
      <div>
        <b>{{ t("additionalFunction", 2) }}</b>
        <br />
        <div
          v-for="(fonction, index) in currentPersonne.additionalFonctions"
          :key="index"
        >
          {{ fonction }}
        </div>
      </div>
    </div>
    <div
      v-if="
        (currentTab == Tabs.AdministrativeStaff ||
          currentTab == Tabs.TeachingStaff) &&
        isAddMode
      "
    >
      <div v-for="(filiere, index) in customMapping?.filieres" :key="index">
        <div>
          <b>{{ filiere.libelleFiliere }}</b>
        </div>
        <div class="d-flex flex-row flex-wrap">
          <div
            v-for="(discipline, index) in filiere.disciplines"
            :key="index"
            class="flex-item"
          >
            <v-checkbox
              v-model="selected"
              :label="discipline.disciplinePoste"
              :value="`${filiere.id}-${discipline.id}`"
              color="primary"
              :hide-details="true"
            />
          </div>
        </div>
      </div>
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

<style scoped lang="scss">
.flex-item {
  width: 100%;
}

@media (min-width: 700px) {
  .flex-item {
    width: 50%;
  }
}
</style>
