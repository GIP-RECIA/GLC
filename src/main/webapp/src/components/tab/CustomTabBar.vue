<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import CustomTabItem from '@/components/tab/CustomTabItem.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { storeToRefs } from 'pinia';
import { onMounted } from 'vue';
import { useRouter } from 'vue-router';

const configurationStore = useConfigurationStore();
const { structures, appTab } = storeToRefs(configurationStore);

const router = useRouter();

const close = (event: { id: number; selected: boolean }): void => {
  structures.value.splice(event.id, 1);
  if (!event.selected) return;

  // Definition du nouvel onglet
  const newCurrentStructure = appTab.value! - 1;
  appTab.value = newCurrentStructure >= 0 ? newCurrentStructure : 0;

  // Changement de page
  const structureId = structures.value.find((structure, index) => index == appTab.value)?.id;
  if (structureId) router.push({ name: 'structure', params: { structureId: structureId } });
  else newTab();
};

const newTab = (): void => {
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
      <transition-group name="tab-bar">
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
