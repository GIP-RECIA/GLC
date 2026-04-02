<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
import { faPen, faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { storeToRefs } from 'pinia'
import { useI18n } from 'vue-i18n'
import FonctionsLayout from '@/components/layouts/FonctionsLayout.vue'
import { usePersonne } from '@/composables/index.ts'
import { useConfigurationStore, usePersonneStore } from '@/stores/index.ts'

defineProps<{
  user?: Personne
}>()

const configurationStore = useConfigurationStore()
const { allFilieres } = storeToRefs(configurationStore)

const personneStore = usePersonneStore()
const { personneStructure } = storeToRefs(personneStore)

const { t } = useI18n()

const { canEditAdditionals } = usePersonne()
</script>

<template>
  <h2>Strcture</h2>

  <h3>{{ t('person.information.function', 2) }}</h3>

  <FonctionsLayout
    :filieres="allFilieres"
    :fonctions="personneStructure.fonctions"
  />

  <h3>{{ t('person.information.additionalFunction', 2) }}</h3>

  <FonctionsLayout
    :filieres="allFilieres"
    :fonctions="personneStructure.additionalFonctions"
    :clickable="canEditAdditionals"
  />

  <button
    type="button"
    class="btn-primary small"
  >
    {{ t(
      personneStructure.additionalFonctions
        && personneStructure.additionalFonctions.length > 0
        ? 'button.edit'
        : 'button.add',
    )
    }}
    <FontAwesomeIcon
      :icon="
        personneStructure.additionalFonctions
          && personneStructure.additionalFonctions.length > 0
          ? faPen
          : faPlus
      "
    />
  </button>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
