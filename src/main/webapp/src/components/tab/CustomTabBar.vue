<script setup lang="ts">
import CustomTabItem from '@/components/tab/CustomTabItem.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { storeToRefs } from 'pinia';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

const configurationStore = useConfigurationStore();
const { structures, appTab } = storeToRefs(configurationStore);

const router = useRouter();

const close = (i: number) => {
  structures.value.splice(i, 1);

  // Definition du nouvel onglet
  const newCurrentStructure = appTab.value! - 1;
  appTab.value = newCurrentStructure >= 0 ? newCurrentStructure : 0;

  // Changement de page
  const structureId = structures.value.find((structure, index) => index == appTab.value)?.id;
  if (structureId) router.push({ name: 'structure', params: { structureId: structureId } });
  else newTab();
};

const newTab = () => {
  router.push({ name: 'home' });
  appTab.value = undefined;
};

onMounted(() => {
  const tabBar = document.querySelector('#tab-bar.scrollable-x');
  tabBar?.addEventListener('wheel', (event) => {
    event.preventDefault();
    tabBar.scrollLeft += (event as WheelEvent).deltaY;
  });
});
</script>

<template>
  <div id="tab-bar" class="scrollable-x">
    <div class="d-flex my-2">
      <transition-group name="custom">
        <custom-tab-item
          v-for="(structure, index) in structures"
          :key="index"
          :id="index"
          :title="structure.name"
          :link="{ name: 'structure', params: { structureId: structure.id } }"
          :selected="appTab == index"
          @close="close"
        />
      </transition-group>
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
  </div>
</template>

<style scoped lang="scss">
.scrollable-x {
  overflow-x: scroll;
  scrollbar-width: none;
}
</style>
