<script setup lang="ts">
import FilieresLayout from "@/components/layouts/FilieresLayout.vue";
import { useConfigurationStore } from "@/stores/configurationStore";
import { useFonctionStore } from "@/stores/fonctionStore";
import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const configurationStore = useConfigurationStore();
const { isAdditional } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
const { teaching } = storeToRefs(fonctionStore);

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
    <filieres-layout :filieres="teaching" :show-all="showAll" />

    <div class="fab ma-4">
      <v-btn
        variant="tonal"
        icon="fas fa-user-pen"
        @click="isAdditional = true"
      />
    </div>
  </v-container>
</template>
