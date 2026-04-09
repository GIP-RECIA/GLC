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
import type { PersonneFonction } from '@/types'
import { faLink, faLock, faLockOpen, faTrashCan } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { storeToRefs } from 'pinia'
import { ref, useTemplateRef, watch } from 'vue'
// import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import ManageAdditionalDialog from '@/components/user/dialogs/ManageAdditionalDialog.vue'
import UserAdministrativeTab from '@/components/user/tabs/UserAdministrativeTab.vue'
import UserInformationTab from '@/components/user/tabs/UserInformationTab.vue'
import { usePersonneStore } from '@/stores/index.ts'
import { Etat } from '@/types/enums/index.ts'

const personneStore = usePersonneStore()
const { initCurrentPersonne } = personneStore
const { currentPersonne } = storeToRefs(personneStore)

const route = useRoute()

// const { t } = useI18n()

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
  'Informations',
  'Référentiel',
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

const fonction = ref<PersonneFonction>()

function editFunction(payload?: PersonneFonction): void {
  fonction.value = payload
  dialogState.value = true
}

/* Actions */

function onToggleLock(): void {
}

function onDelete(): void {
}

function onAttach(): void {
}
</script>

<template>
  <div class="container">
    <h1>{{ currentPersonne?.cn }}</h1>

    <div class="account-actions">
      <h2 class="sr-only">
        Actions
      </h2>

      <ul>
        <li>
          <button
            type="button"
            class="btn-primary small"
            :disabled="
              !currentPersonne?.etat
                || ![Etat.Valide.toString(), Etat.Bloque.toString()].includes(currentPersonne.etat)
            "
            @click="onToggleLock"
          >
            {{
              currentPersonne?.etat === Etat.Bloque.toString()
                ? 'Débloquer'
                : 'Bloquer'
            }}
            <FontAwesomeIcon
              :icon="
                currentPersonne?.etat === Etat.Bloque.toString()
                  ? faLockOpen
                  : faLock"
            />
          </button>
        </li>
        <li>
          <button
            type="button"
            class="btn-primary small"
            :disabled=" !currentPersonne?.etat"
            @click="onDelete"
          >
            {{
              currentPersonne?.etat && currentPersonne.etat === Etat.Deleting.toString()
                ? 'Forcer la suppression'
                : "Supprimer"
            }}
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
            Rattacher
            <FontAwesomeIcon
              :icon="faLink"
            />
          </button>
        </li>
      </ul>
    </div>

    <div class="account-tabs">
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
            {{ tab }}
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

  > h1 {
    margin-bottom: 0;
  }

  > .account-actions {
    > ul {
      @include unstyled-list;
      display: flex;
      flex-wrap: wrap;
      justify-content: end;
      gap: 8px;
    }
  }

  > .account-tabs {
    display: flex;
    flex-direction: column;
    gap: 16px;

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
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;

    > .account-tabs > [role='tabpanel'] {
      gap: 48px;
    }
  }
}
</style>
