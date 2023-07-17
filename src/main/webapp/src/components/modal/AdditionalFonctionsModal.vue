<script setup lang="ts">
import SearchPersonne from "@/components/SearchPersonne.vue";
import BaseModal from "@/components/modal/BaseModal.vue";
import { setPersonneAdditionalFonctions } from "@/services/personneService";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useFonctionStore } from "@/stores/fonctionStore";
import { usePersonneStore } from "@/stores/personneStore";
import { storeToRefs } from "pinia";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { isAdditionalFonction } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { customMapping } = storeToRefs(fonctionStore);

const personneStore = usePersonneStore();
const { administrativeSearchList } = storeToRefs(personneStore);

const selectedUser = ref<number | undefined>();
const selected = ref<Array<string>>([]);

const isSelected = computed<boolean>(() => selected.value.length > 0);

const isSelectedUser = computed<boolean>(() => selectedUser.value != undefined);

const setSelectedUser = (id: number | undefined) => {
  selectedUser.value = id;
};

const save = () => {
  isAdditionalFonction.value = false;
  setPersonneAdditionalFonctions(selectedUser.value!, selected.value);
};
</script>

<template>
  <base-modal v-model="isAdditionalFonction" :title="t('add')">
    <search-personne
      :search-list="administrativeSearchList"
      @update:select="setSelectedUser"
    />
    <div>
      <b>{{ t("additionalFunction", 2) }}</b>
    </div>
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
