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
import { faLink, faPlus } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { storeToRefs } from 'pinia'
import { ref, useTemplateRef, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import AttachDialog from '@/components/old-ui/dialogs/AttachDialog.vue'
import QuickAddDialog from '@/components/old-ui/dialogs/QuickAddDialog.vue'
import AccountTab from '@/components/old-ui/tabs/structure/AccountTab.vue'
import CategorieTab from '@/components/old-ui/tabs/structure/CategorieTab.vue'
import DashboardTab from '@/components/old-ui/tabs/structure/DashboardTab.vue'
import StructureInfo from '@/components/structure/StructureInfo.vue'
import StructureAccountsTab from '@/components/structure/tabs/StructureAccountsTab.vue'
import StructureCompTab from '@/components/structure/tabs/StructureCompTab.vue'
import StructureDashboardTab from '@/components/structure/tabs/StructureDashboardTab.vue'
import { useConfigurationStore, useStructureStore } from '@/stores/index.ts'
import { Tabs } from '@/types/enums/index.ts'

const configurationStore = useConfigurationStore()
const { isAttach } = storeToRefs(configurationStore)

const structureStore = useStructureStore()
const { initCurrentEtab } = structureStore
const { currentEtab, fonction } = storeToRefs(structureStore)

const { t } = useI18n()
const route = useRoute()

watch(
  () => route.params.structureId,
  (newValue) => {
    if (typeof newValue !== 'undefined' && newValue !== null)
      initCurrentEtab(Number(newValue))
  },
  { immediate: true },
)

const show = ref<boolean>(false)

/* Tabs */

const tabsRefs = useTemplateRef<HTMLButtonElement[]>('tab-refs')

const tabs = [
  'dashboard',
  'comp',
  'accounts',
]

const activeTab = ref<number>(0)

function setActiveTab(tab: number, focus: boolean = false): void {
  activeTab.value = tab
  if (focus && tabsRefs.value)
    tabsRefs.value[tab]?.focus()
}

function changeActiveTab(e: KeyboardEvent): void {
  let index: number | undefined
  const active = activeTab.value

  switch (e.key) {
    case 'ArrowLeft':
      e.preventDefault()
      index = active - 1 > -1
        ? active - 1
        : tabs.length - 1
      break
    case 'ArrowRight':
      e.preventDefault()
      index = active + 1 < tabs.length
        ? active + 1
        : 0
      break
    case 'Home':
      e.preventDefault()
      index = 0
      break
    case 'End':
      e.preventDefault()
      index = tabs.length - 1
      break
    default:
      index = undefined
      break
  }

  if (
    index === undefined
    || active === index
  ) {
    return
  }

  setActiveTab(index, true)
}
</script>

<template>
  <div class="container">
    <header>
      <StructureInfo
        :structure="currentEtab"
      />

      <div class="structure-actions">
        <h2 class="sr-only">
          {{ t('page.structure.actions') }}
        </h2>

        <ul>
          <li>
            <button
              type="button"
              class="btn-primary small"
            >
              Rattacher
              <FontAwesomeIcon
                :icon="faLink"
              />
            </button>
          </li>
          <li>
            <button
              type="button"
              class="btn-primary small"
            >
              Créer
              <FontAwesomeIcon
                :icon="faPlus"
              />
            </button>
          </li>
        </ul>
      </div>
    </header>

    <ul
      role="tablist"
      class="tab-selector"
    >
      <li
        v-for="(tab, index) in tabs"
        :key="index"
      >
        <button
          :id="`structure-tab-${index}`"
          ref="tab-refs"
          role="tab"
          :aria-selected="activeTab === index"
          :aria-controls="`structure-tabpanel-${index}`"
          :tabindex="activeTab === index ? '0' : '-1'"
          class="tag"
          :class="{
            active: activeTab === index,
          }"
          @click="setActiveTab(index)"
          @keydown="changeActiveTab"
        >
          {{ t(`page.structure.tab.${tab}`) }}
        </button>
      </li>
    </ul>

    <StructureDashboardTab
      v-show="activeTab === 0"
      id="structure-tabpanel-0"
      role="tabpanel"
      aria-labelledby="structure-tab-0"
      tabindex="0"
      :structure="currentEtab"
    />

    <StructureCompTab
      v-show="activeTab === 1"
      id="structure-tabpanel-1"
      role="tabpanel"
      aria-labelledby="structure-tab-1"
      tabindex="0"
      :structure="currentEtab"
    />

    <StructureAccountsTab
      v-show="activeTab === 2"
      id="structure-tabpanel-2"
      role="tabpanel"
      aria-labelledby="structure-tab-2"
      tabindex="0"
      :structure="currentEtab"
    />
  </div>

  <div
    v-dev
    class="to-remove"
  >
    <DashboardTab />

    <input
      id="showOld"
      v-model="show"
      type="checkbox"
    >
    <label for="showOld">Afficher les onglets par catégorie</label>

    <template v-if="show">
      <h2>
        {{ t('tab.teaching') }}
      </h2>
      <CategorieTab :categorie="Tabs.Teaching" />

      <h2>
        {{ t('tab.school') }}
      </h2>
      <CategorieTab :categorie="Tabs.School">
        <template #actions>
          <v-btn
            v-if="fonction?.customMapping"
            variant="tonal"
            prepend-icon="fas fa-link"
            class="d-none d-sm-flex me-2 custom-height"
            @click="isAttach = true"
          >
            {{ t('button.attach') }}
          </v-btn>
        </template>
        <template #footer>
          <div class="fab ma-4 d-sm-none">
            <v-btn
              v-if="fonction?.customMapping"
              variant="tonal"
              size="x-large"
              icon="fas fa-link"
              @click="isAttach = true"
            />
          </div>
        </template>
      </CategorieTab>

      <h2>
        {{ t('tab.collectivity') }}
      </h2>
      <CategorieTab :categorie="Tabs.Collectivity" />

      <h2>
        {{ t('tab.academic') }}
      </h2>
      <CategorieTab :categorie="Tabs.Academic" />
    </template>

    <h2>
      {{ t('tab.accounts') }}
    </h2>
    <AccountTab />

    <AttachDialog />
    <QuickAddDialog />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.container {
  margin-top: 32px;
  margin-bottom: 40px;
  display: flex;
  flex-direction: column;
  gap: 16px;

  > header {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > .structure-info {
      flex: 1 1 auto;
    }

    > .structure-actions {
      flex: 0 1 auto;
      min-width: 320 - 2 * 16px;

      > ul {
        @include unstyled-list;
        display: flex;
        flex-wrap: wrap;
        justify-content: end;
        gap: 8px;
      }
    }
  }

  > ul {
    @include unstyled-list;
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
  }

  > [role='tabpanel'] {
    display: flex;
    flex-direction: column;
    gap: 32px;

    &:focus-visible {
      outline: 2px dotted var(--#{$prefix}system-blue);
      outline-offset: 8px;
      border-radius: 10px;
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;
    gap: 32px;

    > [role='tabpanel'] {
      gap: 48px;
    }
  }

  @media (width >= map.get($grid-breakpoints, lg)) {
    > header {
      flex-direction: row;
    }
  }
}
</style>
