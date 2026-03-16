<script setup lang="ts">
import type { SimpleEtablissement } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import { concatenate } from '@/utils'

defineProps<{
  searchList?: SimpleEtablissement[]
  variant?: 'outlined' | 'plain' | 'filled' | 'underlined' | 'solo' | 'solo-inverted' | 'solo-filled'
  chips?: boolean
}>()

const { t } = useI18n()

const modelValue = defineModel<number | undefined>()
</script>

<template>
  <v-autocomplete
    v-model="modelValue"
    :items="searchList"
    item-value="id"
    item-title="nom"
    :filter-keys="['raw.nom', 'raw.type', 'raw.uai']"
    no-data-text="search.noResults"
    :placeholder="t('')"
    density="compact"
    :variant="variant"
    rounded="xl"
    hide-details
  >
    <template #item="{ props: itemProps, item }">
      <v-list-item
        v-bind="itemProps"
        :subtitle="
          concatenate(
            [item.raw.type, item.raw.uai],
            ' - ',
          )
        "
      >
        <template #title>
          {{ item.raw.nom }}
        </template>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>
