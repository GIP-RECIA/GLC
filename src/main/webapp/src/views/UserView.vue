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
import type { UserFunction } from '@/types/index.ts'
import {
  faLink,
  faLock,
  faLockOpen,
  faRotateLeft,
  faTrashCan,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { storeToRefs } from 'pinia'
import { computed, ref, useTemplateRef, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import ManageAdditionalDialog from '@/components/user/dialogs/ManageAdditionalDialog.vue'
import UserAdministrativeTab from '@/components/user/tabs/UserAdministrativeTab.vue'
import UserInformationTab from '@/components/user/tabs/UserInformationTab.vue'
import UserInfo from '@/components/user/UserInfo.vue'
import {
  deletePersonne,
  forceDeletePersonne,
  lockPersonne,
  undoDeletePersonne,
  unlockPersonne,
} from '@/services/api/index.ts'
import { usePersonneStore } from '@/stores/index.ts'
import { Etat } from '@/types/enums/index.ts'

const { t } = useI18n()

const personneStore = usePersonneStore()
const { initCurrentPersonne } = personneStore
const { currentPersonne } = storeToRefs(personneStore)

const route = useRoute()

watch(
  () => route.params.userId,
  (userId) => {
    if (userId && typeof userId === 'string')
      initCurrentPersonne(Number(userId))
  },
  {
    immediate: true,
  },
)

/* Tabs */

const tabsRefs = useTemplateRef<HTMLButtonElement[]>('tab-refs')

const tabs = [
  'info',
  'more',
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

/* Dialog */

const dialogState = ref<boolean>(false)

const fonction = ref<UserFunction>()

function editFunction(payload?: UserFunction): void {
  fonction.value = payload
  dialogState.value = true
}

/* Actions */

const canToggleLock = computed<boolean>(() => (
  currentPersonne.value !== undefined
  && [
    Etat.Valide,
    Etat.Bloque,
  ].includes(currentPersonne.value.etat)
))

const isLocked = computed<boolean>(() => (
  currentPersonne.value?.etat === Etat.Bloque
))

async function onToggleLock(): Promise<void> {
  if (!currentPersonne.value)
    return

  const { id } = currentPersonne.value
  const response = isLocked.value
    ? await unlockPersonne(id)
    : await lockPersonne(id)
  if (!response)
    return

  currentPersonne.value.etat = isLocked.value
    ? Etat.Valide
    : Etat.Bloque
}

const isDeleting = computed<boolean>(() => (
  currentPersonne.value?.etat === Etat.Deleting
))

async function onUndoDelete(): Promise<void> {
  if (!currentPersonne.value)
    return

  const { id } = currentPersonne.value
  const response = await undoDeletePersonne(id)
  if (!response)
    return

  currentPersonne.value.etat = Etat.Valide
}

async function onDelete(): Promise<void> {
  if (!currentPersonne.value)
    return

  const { id } = currentPersonne.value
  const response = isDeleting.value
    ? await forceDeletePersonne(id)
    : await deletePersonne(id)
  if (!response)
    return

  currentPersonne.value.etat = isDeleting.value
    ? Etat.Delete
    : Etat.Deleting
}

function onAttach(): void {
}
</script>

<template>
  <div class="container">
    <header>
      <UserInfo
        :user="currentPersonne"
      />

      <div class="user-actions">
        <h2 class="sr-only">
          {{ t('page.user.actions') }}
        </h2>

        <ul>
          <li>
            <button
              type="button"
              class="btn-primary small"
              :disabled="!canToggleLock"
              @click="onToggleLock"
            >
              {{ t(`button.${isLocked ? 'unlock' : 'lock'}`) }}
              <FontAwesomeIcon
                :icon="isLocked ? faLockOpen : faLock"
              />
            </button>
          </li>
          <li
            v-if="isDeleting"
          >
            <button
              type="button"
              class="btn-primary small"
              :disabled="!currentPersonne?.etat"
              @click="onUndoDelete"
            >
              {{ t('button.undoDelete') }}
              <FontAwesomeIcon
                :icon="faRotateLeft"
              />
            </button>
          </li>
          <li>
            <button
              type="button"
              class="btn-primary small"
              :disabled="!currentPersonne?.etat"
              @click="onDelete"
            >
              {{ t(`button.${isDeleting ? 'forceDelete' : 'delete'}`) }}
              <FontAwesomeIcon
                :icon="faTrashCan"
              />
            </button>
          </li>
          <li>
            <button
              type="button"
              class="btn-primary small"
              :disabled="!currentPersonne?.etat"
              @click="onAttach"
            >
              {{ t('button.attach') }}
              <FontAwesomeIcon
                :icon="faLink"
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
          :id="`user-tab-${index}`"
          ref="tab-refs"
          role="tab"
          :aria-selected="activeTab === index"
          :aria-controls="`user-tabpanel-${index}`"
          :tabindex="activeTab === index ? '0' : '-1'"
          class="tag"
          :class="{
            active: activeTab === index,
          }"
          @click="setActiveTab(index)"
          @keydown="changeActiveTab"
        >
          {{ t(`page.user.tab.${tab}`) }}
        </button>
      </li>
    </ul>

    <UserInformationTab
      v-show="activeTab === 0"
      id="user-tabpanel-0"
      role="tabpanel"
      aria-labelledby="user-tab-0"
      tabindex="0"
      :user="currentPersonne"
    />

    <UserAdministrativeTab
      v-show="activeTab === 1"
      id="user-tabpanel-1"
      role="tabpanel"
      aria-labelledby="user-tab-1"
      tabindex="0"
      :user="currentPersonne"
      @edit-function="editFunction"
    />
  </div>

  <ManageAdditionalDialog
    v-model="dialogState"
    :fonction="fonction"
    @update:model-value="dialogState = false"
  />
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

    > .user-info {
      flex: 1 1 auto;
    }

    > .user-actions {
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
