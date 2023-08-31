<script setup lang="ts">
import CustomTabItem from '@/components/tab/CustomTabItem.vue';
import { useConfigurationStore } from '@/stores/configurationStore';
import { storeToRefs } from 'pinia';
import { useRouter } from 'vue-router';

const configurationStore = useConfigurationStore();
const { structures, currentStructure } = storeToRefs(configurationStore);

const router = useRouter();

const close = (i: number) => {
  structures.value.splice(i, 1);

  // Definition du nouvel onglet
  const newCurrentStructure = currentStructure.value! - 1;
  currentStructure.value = newCurrentStructure >= 0 ? newCurrentStructure : 0;

  // Changement de page
  const structureId = structures.value.find((structure, index) => index == currentStructure.value)?.id;
  if (structureId) router.push({ name: 'structure', params: { structureId: structureId } });
  else newTab();
};

const newTab = () => {
  router.push({ name: 'home' });
  currentStructure.value = undefined;
};
</script>

<template>
  <div class="d-flex">
    <custom-tab-item
      v-for="(structure, index) in structures"
      :key="index"
      :id="index"
      :title="structure.name"
      :link="{ name: 'structure', params: { structureId: structure.id } }"
      :selected="currentStructure == index"
      @close="close"
    />
    <v-btn
      v-if="structures.length > 0"
      :to="{ name: 'home' }"
      variant="text"
      density="comfortable"
      icon
      @click="newTab"
    >
      <v-icon icon="fas fa-plus" size="x-small" />
    </v-btn>
  </div>
</template>
