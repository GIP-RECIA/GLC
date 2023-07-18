<script setup lang="ts">
import SearchPersonne from "@/components/SearchPersonne.vue";
import CheckboxLayout from "@/components/layout/CheckboxLayout.vue";
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

const setSelected = (value: Array<string>) => {
  selected.value = value;
};

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
    <checkbox-layout
      :filieres="customMapping?.filieres ? customMapping.filieres : []"
      @update:selected="setSelected"
    />
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
