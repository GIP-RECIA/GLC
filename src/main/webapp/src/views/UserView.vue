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
import type { FunctionForm, UserStructure } from '@/types/index.ts'
import {
  faLink,
  faLock,
  faLockOpen,
  faRotateLeft,
  faTrashCan,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, useTemplateRef, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRouter } from 'vue-router'
import ManageAdditionalDialog from '@/components/accounts/user/dialogs/ManageAdditionalDialog.vue'
import UserAdministrativeTab from '@/components/accounts/user/tabs/UserAdministrativeTab.vue'
import UserInformationTab from '@/components/accounts/user/tabs/UserInformationTab.vue'
import UserInfo from '@/components/accounts/user/UserInfo.vue'
import { useTabs } from '@/composables/index.ts'
import {
  deleteUser,
  forceDeleteUser,
  lockUser,
  undoDeleteUser,
  unlockUser,
} from '@/services/api/index.ts'
import { useUserQuery } from '@/services/queries/index.ts'
import { Etat } from '@/types/enums/index.ts'
import { errorHandler } from '@/utils/index.ts'

const { t } = useI18n()

const router = useRouter()

const { data: user, error } = useUserQuery()

watch(
  error,
  (e) => {
    if (e) {
      errorHandler(e, 'initdata')
      router.replace({ name: 'account' })
    }
  },
)

/* Tabs */

const tabs = [
  'info',
  'administrative',
]

const tabsRefs = useTemplateRef<HTMLButtonElement[]>('tab-refs')

const {
  activeTab,
  changeActiveTab,
  setActiveTab,
} = useTabs({
  tabs,
  tabsRefs,
})

/* Dialog */

const dialogState = ref<boolean>(false)

const structure = ref<UserStructure>()

const fonction = ref<FunctionForm>()

function editFunction(struct: UserStructure, fun?: FunctionForm): void {
  structure.value = struct
  fonction.value = fun
  dialogState.value = true
}

/* Actions */

const canToggleLock = computed<boolean>(() => (
  user.value !== undefined
  && [
    Etat.Valide,
    Etat.Bloque,
  ].includes(user.value.etat)
))

const isLocked = computed<boolean>(() => (
  user.value?.etat === Etat.Bloque
))

async function onToggleLock(): Promise<void> {
  if (!user.value)
    return

  const { id } = user.value
  const response = isLocked.value
    ? await unlockUser(id)
    : await lockUser(id)
  if (!response)
    return

  user.value.etat = isLocked.value
    ? Etat.Valide
    : Etat.Bloque
}

const isDeleting = computed<boolean>(() => (
  user.value?.etat === Etat.Deleting
))

async function onUndoDelete(): Promise<void> {
  if (!user.value)
    return

  const { id } = user.value
  const response = await undoDeleteUser(id)
  if (!response)
    return

  user.value.etat = Etat.Valide
}

async function onDelete(): Promise<void> {
  if (!user.value)
    return

  const { id } = user.value
  const response = isDeleting.value
    ? await forceDeleteUser(id)
    : await deleteUser(id)
  if (!response)
    return

  user.value.etat = isDeleting.value
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
        :user="user"
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
              :disabled="!user?.etat"
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
              :disabled="!user?.etat"
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
              :disabled="!user?.etat"
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
          type="button"
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
      :user="user"
    />

    <UserAdministrativeTab
      v-show="activeTab === 1"
      id="user-tabpanel-1"
      role="tabpanel"
      aria-labelledby="user-tab-1"
      tabindex="0"
      :user="user"
      @edit-function="editFunction"
    />
  </div>

  <ManageAdditionalDialog
    v-model="dialogState"
    :user="user"
    :structure="structure"
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
