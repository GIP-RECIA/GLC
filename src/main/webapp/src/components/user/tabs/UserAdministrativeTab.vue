<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
import { faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n'
import { usePersonne } from '@/composables/index.ts'
import { useConfigurationStore } from '@/stores/index.ts'
import UserFunctions from './administrative/UserFunctions.vue'

defineProps<{
  user?: Personne
}>()

const configurationStore = useConfigurationStore()
const { allFilieres } = storeToRefs(configurationStore)

const { t } = useI18n()

const { canEditAdditionals } = usePersonne()
</script>

<template>
  <div>
    <div
      v-for="structure in user?.listeStructures"
      :key="`user-administrative-structure-${structure.id}`"
      class="structure-functions"
    >
      <h2>
        {{ structure.nom }}
        <span v-show="structure.type">
          {{ structure.type }}
          <span v-show="structure.uai">
            {{ structure.uai }}
          </span>
        </span>
      </h2>

      <div class="r-card">
        <header>
          <h3>
            {{ t('person.information.function', 2) }}
          </h3>
        </header>

        <div class="body">
          <UserFunctions
            :filieres="allFilieres"
            :fonctions="structure.fonctions"
          />
        </div>
      </div>

      <div class="r-card">
        <header>
          <h3>
            {{ t('person.information.additionalFunction', 2) }}
          </h3>
        </header>

        <div class="body">
          <UserFunctions
            :filieres="allFilieres"
            :fonctions="structure.additionalFonctions"
            :clickable="canEditAdditionals"
          />
        </div>

        <footer>
          <button
            type="button"
            class="btn-primary small"
          >
            {{ t('button.add') }}
            <FontAwesomeIcon
              :icon="faPlus"
            />
          </button>
        </footer>
      </div>

      <div class="r-card">
        <header>
          <h3>
            Classes
          </h3>
        </header>

        <div class="body">
          <ul v-if="[].length > 0">
            <li
              v-for="uClass in []"
              :key="`class-${uClass}`"
              class="tag-primary"
            >
              {{ }}
            </li>
          </ul>
        </div>
      </div>

      <div class="r-card">
        <header>
          <h3>
            Groupes pédagogiques
          </h3>
        </header>

        <div class="body">
          <ul v-if="[].length > 0">
            <li
              v-for="group in []"
              :key="`educational-group${group}`"
              class="tag-primary"
            >
              {{ }}
            </li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.structure-functions {
  display: grid;
  gap: 16px;

  > h2 {
    margin-bottom: 0;

    > span {
      opacity: 0.6;
      font-size: var(--#{$prefix}font-size-sm);
    }
  }

  > .r-card > .body {
    display: flex;
    flex-direction: column;
    gap: 16px;
  }

  @media (width >= map.get($grid-breakpoints, lg)) {
    grid-template-columns: repeat(2, 1fr);
    align-items: start;

    > h2 {
      grid-column: span 2;
    }
  }
}
</style>
