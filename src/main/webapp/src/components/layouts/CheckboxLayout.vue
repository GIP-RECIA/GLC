<script setup lang="ts">
import type { Filiere } from '@/types/filiereType';
import { ref } from 'vue';

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  selected?: Array<string>;
  disabled?: Array<string>;
}>();

const emit = defineEmits<(event: 'update:selected', payload: Array<string>) => void>();

const checked = ref<Array<string>>(props.selected ? props.selected : []);

const updateSelected = () => {
  emit('update:selected', checked.value);
};
</script>

<template>
  <div v-for="(filiere, index) in filieres" :key="index">
    <div>
      <b>{{ filiere.libelleFiliere }}</b>
    </div>
    <div class="d-flex flex-row flex-wrap">
      <div v-for="(discipline, index) in filiere.disciplines" :key="index" class="modal-flex-item">
        <v-checkbox
          v-model="checked"
          :label="discipline.disciplinePoste"
          :value="`${filiere.id}-${discipline.id}`"
          :disabled="disabled?.includes(`${filiere.id}-${discipline.id}`)"
          color="primary"
          :hide-details="true"
          @update:model-value="updateSelected"
        />
      </div>
    </div>
  </div>
</template>
