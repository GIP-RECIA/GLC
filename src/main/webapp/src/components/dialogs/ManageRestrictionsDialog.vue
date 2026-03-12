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
import type { StructureRestriction } from '@/types/index.ts'
import { format, formatISO } from 'date-fns'
import { computed, ref, watchEffect } from 'vue'

const props = defineProps<{
  restrictions?: StructureRestriction
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'save': []
}>()

const modelValue = defineModel<boolean>({ required: true })

const EMPTY_RESTRICTION: StructureRestriction = {
  dateRentreeEtab: null,
  niveaux: [],
}

const restrictions = computed(() => props.restrictions ?? EMPTY_RESTRICTION)

const restrictionsToDisplay = ref<StructureRestriction>(EMPTY_RESTRICTION)

const canSave = computed<boolean>(() => {
  return true
})

function save() {
  emit(
    'save',
  )
  close()
}

function close() {
  emit('update:modelValue', false)
}

function toDateTime(date: string | null): string | null {
  return date !== null
    ? `${formatISO(date, { representation: 'date' })}T${format(date, 'HH:mm')}`
    : null
}

watchEffect(() => {
  const niveaux = restrictions.value.niveaux.map((niveau) => {
    const classes = niveau.classes.map((classe) => {
      return {
        ...classe,
        dateRentreeClasse: toDateTime(classe.dateRentreeClasse),
      }
    })

    return {
      ...niveau,
      dateRentreeNiveau: toDateTime(niveau.dateRentreeNiveau),
      classes,
    }
  })

  restrictionsToDisplay.value = {
    dateRentreeEtab: toDateTime(restrictions.value.dateRentreeEtab),
    niveaux,
  }
})
</script>

<template>
  <v-dialog
    v-model="modelValue"
    scrollable
    :max-width="1024"
  >
    <v-card rounded="xl">
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title>
          <h1 class="mb-0">
            Date de rentrée
          </h1>
        </v-toolbar-title>
        <template #append>
          <v-btn
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            class="me-1"
            @click="close"
          />
        </template>
      </v-toolbar>

      <v-card-text class="pt-0 py-3 manage-restrictions-dialog-container">
        <label for="global">
          Valeur globale
        </label>
        <input
          id="global"
          v-model="restrictionsToDisplay.dateRentreeEtab"
          type="datetime-local"
        >

        <ul>
          <li
            v-for="(niveau, index) in restrictionsToDisplay.niveaux"
            :key="niveau.niveau"
          >
            <div>
              <label :for="`niveau-${index}`">
                {{ niveau.niveau }}
              </label>
              <input
                :id="`niveau-${index}`"
                v-model="niveau.dateRentreeNiveau"
                type="datetime-local"
              >
            </div>

            <ul>
              <li
                v-for="(classe, index2) in niveau.classes"
                :key="classe.classe"
              >
                <div>
                  <label :for="`niveau-${index}-classe-${index2}`">
                    {{ classe.classe }}
                  </label>
                  <input
                    :id="`niveau-${index}-classe-${index2}`"
                    v-model="classe.dateRentreeClasse"
                    type="datetime-local"
                  >
                </div>
              </li>
            </ul>
          </li>
        </ul>
      </v-card-text>

      <v-card-actions>
        <button
          class="btn-secondary"
          @click="close"
        >
          Annuler
          <font-awesome-icon icon="fas fa-xmark" />
        </button>
        <button
          :disabled="!canSave"
          class="btn-primary"
          @click="save"
        >
          Enregistrer
          <font-awesome-icon icon="fas fa-floppy-disk" />
        </button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/mixins' as *;

.manage-restrictions-dialog-container {
  display: flex;
  flex-direction: column;
  gap: 16px;

  > ul {
    display: grid;
    gap: 16px;

    > li {
      display: flex;
      flex-direction: column;
      gap: 16px;
      border: 1px solid grey;
      border-radius: 10px;
      padding: 16px;

      > div {
        display: flex;
        flex-direction: column;
        gap: 2px;
      }

      > ul {
        display: grid;
        gap: 16px;

        > li > div {
          display: flex;
          flex-direction: column;
          gap: 2px;
        }
      }
    }
  }
}

ul {
  @include unstyled-list;
}

fieldset {
  border: none;
}

input[type='datetime-local'] {
  background-color: lightgray;
  border-bottom: 1px solid grey;
}

@media (width >= 700px) {
  .manage-restrictions-dialog-container {
    > ul > li > ul {
      grid-template-columns: 1fr 1fr;
    }
  }
}
</style>
