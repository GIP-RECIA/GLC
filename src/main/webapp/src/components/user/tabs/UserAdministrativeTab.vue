<script setup lang="ts">
import type { User, UserFunction } from '@/types/index.ts'
import { faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import UserFunctions from './administrative/UserFunctions.vue'

defineProps<{
  user?: User
}>()

const emit = defineEmits<{
  editFunction: [fonction: UserFunction | undefined]
}>()

const { t } = useI18n()

function addFunction(): void {
  emit('editFunction', undefined)
}

function editFunction(fonction: UserFunction): void {
  emit('editFunction', fonction)
}
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
            {{ t('page.user.function', 2) }}
          </h3>
        </header>

        <div class="body">
          <UserFunctions
            :filieres="[]"
            :fonctions="structure.fonctions"
          />
        </div>
      </div>

      <div class="r-card">
        <header>
          <h3>
            {{ t('page.user.additionalFunction', 2) }}
          </h3>
        </header>

        <!-- TODO: manage rights click -->
        <div class="body">
          <UserFunctions
            :filieres="[]"
            :fonctions="structure.additionalFonctions"
            :clickable="false"
            @tag-click="editFunction"
          />
        </div>

        <footer>
          <button
            type="button"
            class="btn-primary small"
            @click="addFunction"
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
            {{ t('page.user.class', 2) }}
          </h3>
        </header>

        <div class="body">
          <ul v-if="structure.classes.length > 0">
            <li
              v-for="uClass in structure.classes"
              :key="`class-${uClass}`"
              class="tag-primary"
            >
              {{ uClass }}
            </li>
          </ul>
        </div>
      </div>

      <div class="r-card">
        <header>
          <h3>
            {{ t('page.user.educationalGroup', 2) }}
          </h3>
        </header>

        <div class="body">
          <ul
            v-if="structure.groupesPedagogiques.length > 0"
          >
            <li
              v-for="group in structure.groupesPedagogiques"
              :key="`educational-group${group}`"
              class="tag-primary"
            >
              {{ group }}
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

    > ul {
      @include unstyled-list;
      display: flex;
      flex-wrap: wrap;
      gap: 7px 8px;
    }
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
