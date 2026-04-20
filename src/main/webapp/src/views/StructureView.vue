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
import { ref, useTemplateRef, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import StructureInfo from '@/components/structure/StructureInfo.vue'
import StructureAccountsTab from '@/components/structure/tabs/StructureAccountsTab.vue'
import StructureCompTab from '@/components/structure/tabs/StructureCompTab.vue'
import StructureDashboardTab from '@/components/structure/tabs/StructureDashboardTab.vue'
import { useEtablissementQuery } from '@/services/queries/index.ts'
import { errorHandler } from '@/utils/index.ts'

const { t } = useI18n()

const router = useRouter()

const { data, error } = useEtablissementQuery()

watch(
  error,
  (e) => {
    if (e) {
      errorHandler(e, 'initCurrentEtab')
      router.replace({ name: 'account' })
    }
  },
)

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

/* Actions */

function onAttach(): void {

}

function onCreate(): void {

}
</script>

<template>
  <div class="container">
    <header>
      <StructureInfo
        :structure="data"
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
              @click="onAttach"
            >
              {{ t('button.attach') }}
              <FontAwesomeIcon
                :icon="faLink"
              />
            </button>
          </li>
          <li>
            <button
              type="button"
              class="btn-primary small"
              @click="onCreate"
            >
              {{ t('button.create') }}
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
      :structure="data"
    />

    <StructureCompTab
      v-show="activeTab === 1"
      id="structure-tabpanel-1"
      role="tabpanel"
      aria-labelledby="structure-tab-1"
      tabindex="0"
      :structure="data"
    />

    <StructureAccountsTab
      v-show="activeTab === 2"
      id="structure-tabpanel-2"
      role="tabpanel"
      aria-labelledby="structure-tab-2"
      tabindex="0"
      :structure="data"
    />
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
