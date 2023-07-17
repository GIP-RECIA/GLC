<script setup lang="ts">
import FiliereDisciplines from "@/components/layout/FiliereDisciplines.vue";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useFonctionStore } from "@/stores/fonctionStore";
import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { isAdditionalFonction } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { administrative, isCustomMapping } = storeToRefs(fonctionStore);

const showAll = ref<boolean>(false);
</script>

<template>
  <v-container fluid>
    <div class="d-flex justify-end mb-4 mb-sm-0">
      <v-btn
        variant="tonal"
        :prepend-icon="showAll ? 'fas fa-eye-slash' : 'fas fa-eye'"
        :alt="t(showAll ? 'hideEmpty' : 'showAll')"
        @click="showAll = !showAll"
      >
        {{ t(showAll ? "hideEmpty" : "showAll") }}
      </v-btn>
    </div>
    <filiere-disciplines
      v-for="(filiere, index) in administrative"
      :key="index"
      :filiere="filiere"
      :show-all="showAll"
    />

    <div class="fab ma-4">
      <v-btn
        v-if="isCustomMapping"
        variant="tonal"
        icon="fas fa-user-plus"
        @click="isAdditionalFonction = true"
      />
    </div>
  </v-container>
</template>
