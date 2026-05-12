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
import type {
  FunctionForm,
  UserFunction,
  UserStructure,
} from '@/types/index.ts'
import {
  faLink,
  faLock,
  faLockOpen,
  faRotateLeft,
  faTrashCan,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed, ref, useTemplateRef } from 'vue'
import { useI18n } from 'vue-i18n'
import ManageAdditionalDialog from '@/components/accounts/dialogs/ManageAdditionalDialog.vue'
import UserAdministrativeTab from '@/components/accounts/user/tabs/UserAdministrativeTab.vue'
import UserInformationTab from '@/components/accounts/user/tabs/UserInformationTab.vue'
import UserInfo from '@/components/accounts/user/UserInfo.vue'
import { useTabs } from '@/composables/index.ts'
import {
  useDeleteUserMutation,
  useForceDeleteUserMutation,
  useLockUserMutation,
  useUndoDeleteUserMutation,
  useUnlockUserMutation,
  useUserQuery,
} from '@/services/queries/index.ts'
import { Etat } from '@/types/enums/index.ts'

const { t } = useI18n()

const { data: user } = useUserQuery()

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

const dialogData = ref<{
  title: string
  structureId: number | undefined
  disabledFonctions: UserFunction[] | undefined
  editFonction: FunctionForm | undefined
}>({
  title: '',
  structureId: undefined,
  disabledFonctions: undefined,
  editFonction: undefined,
})

function editFunction(
  struct: UserStructure,
  fun: FunctionForm | undefined,
): void {
  dialogData.value = {
    title: t(
      `page.account.dialog.manageAdditional.title.${fun ? 'edit' : 'add'}`,
      {
        structre: struct.nom,
      },
    ),
    structureId: struct.id,
    disabledFonctions: [
      ...struct.fonctions,
      ...struct.additionalFonctions,
    ],
    editFonction: fun,
  }
  dialogState.value = true
}

/* Actions */

const rights = computed<{
  canToggleLock: boolean
  canDelete: boolean
  canUndoDelete: boolean
  canForceDelete: boolean
  canAttach: boolean
  isLocked: boolean
  isDeleting: boolean
}>(() => {
  if (!user.value) {
    return {
      canToggleLock: false,
      canDelete: false,
      canUndoDelete: false,
      canForceDelete: false,
      canAttach: false,
      isLocked: false,
      isDeleting: false,
    }
  }

  const { etat, local } = user.value

  return {
    canToggleLock: [
      Etat.Valide,
      Etat.Bloque,
    ].includes(etat),
    canDelete: local && !(
      etat === Etat.Deleting
      || etat === Etat.Delete
    ),
    canUndoDelete: etat === Etat.Deleting
      || etat === Etat.Delete,
    canForceDelete: etat === Etat.Deleting,
    canAttach: etat === Etat.Invalide
      || etat === Etat.Valide
      || etat === Etat.Bloque,
    isLocked: etat === Etat.Bloque,
    isDeleting: etat === Etat.Deleting,
  }
})

const {
  mutate: lock,
} = useLockUserMutation()
const {
  mutate: unlock,
} = useUnlockUserMutation()

function onToggleLock(): void {
  if (!user.value)
    return

  const { id } = user.value
  rights.value.isLocked
    ? unlock(id)
    : lock(id)
}

const {
  mutate: undoDelete,
} = useUndoDeleteUserMutation()

function onUndoDelete(): void {
  if (!user.value)
    return

  const { id } = user.value
  undoDelete(id)
}

const {
  mutate: deleteU,
} = useDeleteUserMutation()

function onDelete(): void {
  if (!user.value)
    return

  const { id } = user.value
  deleteU(id)
}

const {
  mutate: forceDelete,
} = useForceDeleteUserMutation()

function onForceDelete(): void {
  if (!user.value)
    return

  const { id } = user.value
  forceDelete(id)
}

function onAttach(): void {
  dialogData.value = {
    title: t('page.account.dialog.manageAdditional.title.attach'),
    structureId: undefined,
    disabledFonctions: undefined,
    editFonction: undefined,
  }
  dialogState.value = true
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
          <li v-if="rights.canToggleLock">
            <button
              type="button"
              class="btn-primary small"
              @click="onToggleLock"
            >
              {{ t(`button.${rights.isLocked ? 'unlock' : 'lock'}`) }}
              <FontAwesomeIcon
                :icon="rights.isLocked ? faLockOpen : faLock"
              />
            </button>
          </li>
          <li v-if="rights.canDelete">
            <button
              type="button"
              class="btn-primary small"
              @click="onDelete"
            >
              {{ t('button.delete') }}
              <FontAwesomeIcon
                :icon="faTrashCan"
              />
            </button>
          </li>
          <li v-if="rights.canForceDelete">
            <button
              type="button"
              class="btn-primary small"
              @click="onForceDelete"
            >
              {{ t('button.forceDelete') }}
              <FontAwesomeIcon
                :icon="faTrashCan"
              />
            </button>
          </li>
          <li v-if="rights.canUndoDelete">
            <button
              type="button"
              class="btn-primary small"
              @click="onUndoDelete"
            >
              {{ t('button.undoDelete') }}
              <FontAwesomeIcon
                :icon="faRotateLeft"
              />
            </button>
          </li>
          <li v-if="rights.canAttach">
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
    :title="dialogData.title"
    :user="user"
    :structure-id="dialogData.structureId"
    :disabled-fonctions="dialogData.disabledFonctions"
    :edit-fonction="dialogData.editFonction"
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
