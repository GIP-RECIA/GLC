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
import { faLink, faLock, faLockOpen, faTrashCan } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { storeToRefs } from 'pinia'
import { watch } from 'vue'
// import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import UserAdministrativeTab from '@/components/user/tabs/UserAdministrativeTab.vue'
import UserInformationTab from '@/components/user/tabs/UserInformationTab.vue'
import { usePersonne } from '@/composables/index.ts'
import { usePersonneStore } from '@/stores/index.ts'
import { Etat } from '@/types/enums/index.ts'

const personneStore = usePersonneStore()
const { initCurrentPersonne } = personneStore
const { currentPersonne } = storeToRefs(personneStore)

const {
  hasFunctions,
  canEditAdditionals,
} = usePersonne()

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
</script>

<template>
  <div class="container">
    <h1>{{ currentPersonne?.cn }}</h1>

    <div class="tab-content">
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
              :disabled="!currentPersonne?.etat || !canEditAdditionals"
            >
              Rattacher
              <FontAwesomeIcon
                :icon="faLink"
              />
            </button>
          </li>
        </ul>
      </div>

      <UserInformationTab
        :user="currentPersonne"
      />

      <UserAdministrativeTab
        v-if="hasFunctions"
        :user="currentPersonne"
      />
    </div>
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

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;
  }
}

.tab-content {
  display: flex;
  flex-direction: column;
  gap: 16px;

  > .account-actions {
    > ul {
      @include unstyled-list;
      display: flex;
      flex-wrap: wrap;
      justify-content: end;
      gap: 8px;
    }
  }
}
</style>
