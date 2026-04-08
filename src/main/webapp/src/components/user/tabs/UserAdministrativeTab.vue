<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
import { faPen, faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n'
import FonctionsLayout from '@/components/layouts/FonctionsLayout.vue'
import { usePersonne } from '@/composables/index.ts'
import { useConfigurationStore } from '@/stores/index.ts'

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
      class="structure-functions-card"
    >
      <h2>
        {{ structure.nom }}
      </h2>

      <div class="r-card">
        <header>
          <h3>
            {{ t('person.information.function', 2) }}
          </h3>
        </header>

        <div class="body">
          <FonctionsLayout
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
          <FonctionsLayout
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
            {{ t(
              structure.additionalFonctions.length > 0
                ? 'button.edit'
                : 'button.add',
            )
            }}
            <FontAwesomeIcon
              :icon="
                structure.additionalFonctions.length > 0
                  ? faPen
                  : faPlus
              "
            />
          </button>
        </footer>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.structure-functions-card {
  display: flex;
  flex-direction: column;
  gap: 16px;

  > h2 {
    margin-bottom: 0;
  }
}

.info-container {
  display: flex;
  flex-direction: column;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    align-items: start;
  }
}
</style>
