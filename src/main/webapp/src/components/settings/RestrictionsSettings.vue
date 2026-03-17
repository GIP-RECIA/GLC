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
import { faPen } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { computed } from 'vue'
import SafeEmptyData from '@/components/SafeEmptyData.vue'

const props = withDefaults(
  defineProps<{
    restrictions?: StructureRestriction
    canEdit?: boolean
    disableEdit?: boolean
  }>(),
  {
    canEdit: true,
    disableEdit: false,
  },
)

defineEmits<{
  edit: [boolean]
}>()

const dataToDisplay = computed<
  StructureRestriction | undefined
>(() => {
  if (!props.restrictions)
    return

  const niveaux = props.restrictions.niveaux
    .map((niveau) => {
      const classes = niveau.classes
        .filter(({ dateRentreeClasse }) => dateRentreeClasse !== null)

      return { ...niveau, classes }
    })
    .filter(niveau => (
      niveau.dateRentreeNiveau !== null
      || niveau.classes.length > 0
    ))

  return { ...props.restrictions, niveaux }
})

function toDisplayDate(
  date: string | undefined | null,
): string | undefined {
  return date
    ? format(date, 'P p')
    : undefined
}
</script>

<template>
  <div class="r-card restriction-card">
    <header>
      <h3>Date de rentrée</h3>
    </header>

    <div class="body">
      <div class="item">
        <h4>Etablissement</h4>
        <SafeEmptyData
          :value="toDisplayDate(dataToDisplay?.dateRentreeEtab)"
        />
      </div>

      <div
        v-if="dataToDisplay"
        v-show="dataToDisplay.niveaux.length > 0"
        class="niveau-container"
      >
        <div
          v-for="niveau in dataToDisplay.niveaux"
          :key="niveau.niveau"
          class="niveau-card"
        >
          <div
            v-show="niveau.dateRentreeNiveau !== null"
            class="item"
          >
            <h4>{{ niveau.niveau }}</h4>
            <SafeEmptyData
              :value="toDisplayDate(niveau.dateRentreeNiveau)"
            />
          </div>

          <ul v-show="niveau.classes.length > 0">
            <li
              v-for="classe in niveau.classes"
              :key="classe.classe"
            >
              <div class="item">
                <h5 class="h4">
                  {{ classe.classe }}
                </h5>
                <SafeEmptyData
                  :value="toDisplayDate(classe.dateRentreeClasse)"
                />
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <footer
      v-if="restrictions && canEdit"
    >
      <button
        :disabled="disableEdit"
        class="btn-primary small"
        @click="() => $emit('edit', true)"
      >
        Modifier
        <FontAwesomeIcon :icon="faPen" />
      </button>
    </footer>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.restriction-card {
  display: flex;
  flex-direction: column;

  > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;

    .item {
      > h4,
      > h5 {
        margin-bottom: 4px;
      }
    }

    > .niveau-container {
      display: grid;
      gap: 16px;

      > .niveau-card {
        display: flex;
        flex-direction: column;
        gap: 16px;
        border-radius: 6px;
        border: 1px solid var(--#{$prefix}stroke);
        padding: 16px;

        > ul {
          @include unstyled-list;
          display: grid;
          gap: 16px;
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    > .body {
      > .niveau-container {
        grid-template-columns: repeat(auto-fill, minmax(512px, 1fr));

        > .niveau-card {
          > ul {
            grid-template-columns: repeat(auto-fill, minmax(256px, 1fr));
          }
        }
      }
    }
  }
}
</style>
