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
import type { SimplePersonne } from '@/types'
import type { Etat, Tabs } from '@/types/enums'
import { storeToRefs } from 'pinia'
import { computed } from 'vue'
import SelectFilter from '@/components/filter/SelectFilter.vue'
import FilieresLayout from '@/components/layouts/FilieresLayout.vue'
import PersonneSearch from '@/components/search/personne/PersonneSearch.vue'
import { useConfigurationStore, usePersonneStore, useStructureStore } from '@/stores'

const props = defineProps<{
  categorie: Exclude<Tabs, Tabs.Dashboard | Tabs.Accounts>
}>()
const configurationStore = useConfigurationStore()
const { filterAccountStates, currentStructureConfig } = storeToRefs(configurationStore)

const structureStore = useStructureStore()
const { staff, filieresByStaff } = storeToRefs(structureStore)

const personneStore = usePersonneStore()
const { initCurrentPersonne } = personneStore
const { currentPersonne } = storeToRefs(personneStore)

const accountFilters = computed<Array<Etat>>(() =>
  currentStructureConfig.value ? currentStructureConfig.value[props.categorie].accountStates : [],
)

function setAccountFilters(states: Array<Etat>): void {
  if (!currentStructureConfig.value)
    return
  currentStructureConfig.value[props.categorie].accountStates = states
}

function loadUser(user: SimplePersonne | undefined): void {
  if (!user)
    return
  currentPersonne.value = undefined
  initCurrentPersonne(user.id, true)
}
</script>

<template>
  <v-container fluid>
    <div class="d-flex align-center justify-end mb-4 mb-sm-0">
      <slot name="actions" />
      <PersonneSearch
        :search-list="staff[categorie]"
        :model-value="undefined"
        search-type="IN"
        variant="solo"
        class="w-100 staff-search me-2"
        @update:model-value="loadUser"
      />
      <SelectFilter
        v-if="filterAccountStates"
        :model-value="accountFilters"
        :items="filterAccountStates"
        class="account-filter"
        @update:model-value="setAccountFilters"
      />
    </div>
    <FilieresLayout :filieres="filieresByStaff[categorie]" :account-states="accountFilters" />
    <slot name="footer" />
  </v-container>
</template>

<style scoped lang="scss">
.staff-search {
  max-width: 20em;
}
</style>
