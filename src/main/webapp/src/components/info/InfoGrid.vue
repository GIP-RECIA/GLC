<script setup lang="ts">
import InfoCard from '@/components/info/InfoCard.vue';
import { useStructureStore } from '@/stores/structureStore.ts';
import { storeToRefs } from 'pinia';

const structureStore = useStructureStore();
const { personnesByEtat } = storeToRefs(structureStore);
</script>

<template>
  <div class="info-grid">
    <info-card
      v-for="(elem, index) in personnesByEtat"
      :key="index"
      :etat="elem[0]"
      :value="elem[1] ? elem[1].length : 0"
      variant="flat"
    />
  </div>
</template>

<style scoped lang="scss">
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(15em, 1fr));
  gap: 1em;
}
</style>
