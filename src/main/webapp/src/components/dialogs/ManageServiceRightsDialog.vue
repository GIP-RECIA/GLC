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
import type { RightMember, ServiceRight, ServiceRights } from '@/types/index.ts'
import { computed, ref, watchEffect } from 'vue'
import { alphaSort } from '@/utils/index.ts'

const props = defineProps<{
  serviceRight?: Pick<ServiceRights, 'service'> & ServiceRight
}>()

const emit = defineEmits<{
  'update:modelValue': [value: boolean]
  'save': [
    service: string,
    serviceRight: ServiceRight,
    toAdd: string [],
    toRemove: string [],
  ]
}>()

const modelValue = defineModel<boolean>({ required: true })

const EMPTY_SERVICE_RIGHT: Pick<ServiceRights, 'service'> & ServiceRight = {
  service: '',
  role: '',
  isAdmin: false,
  description: '',
  currentMembers: [],
  possibleGroups: [],
  mandatoryGroups: [],
  allowPeople: false,
}

const serviceRight = computed(() => props.serviceRight ?? EMPTY_SERVICE_RIGHT)

const checkboxes = computed<RightMember[]>(() => [
  ...serviceRight.value.mandatoryGroups,
  ...serviceRight.value.possibleGroups,
].sort((a, b) => alphaSort(a.displayName, b.displayName, 'asc')))

const disabled = computed<string[]>(() =>
  serviceRight.value.mandatoryGroups.map(g => g.id),
)

const checkedGroups = ref<string[]>([])

const currentUsers = ref<string[]>([])

const changes = computed<{
  toAdd: string[]
  toRemove: string[]
}>(() => {
  const existing = props.serviceRight?.currentMembers.map(({ id }) => id)

  const allChecked = [
    ...checkedGroups.value,
    ...currentUsers.value,
  ]

  return {
    toAdd: allChecked.filter(checked => !existing?.includes(checked)),
    toRemove: existing?.filter(checked => !allChecked.includes(checked)) ?? [],
  }
})

const canSave = computed<boolean>(() => {
  const { toAdd, toRemove } = changes.value

  return toAdd.length > 0 || toRemove.length > 0
})

function save() {
  const { toAdd, toRemove } = changes.value
  emit(
    'save',
    serviceRight.value.service,
    serviceRight.value,
    toAdd,
    toRemove,
  )
  close()
}

function close() {
  emit('update:modelValue', false)
}

function removeUser(id: string) {
  currentUsers.value = currentUsers.value.filter(user => user !== id)
}

function addUser(id: string) {
  currentUsers.value.push(id)
}

watchEffect(
  () => {
    if (!modelValue.value || !props.serviceRight)
      return

    checkedGroups.value = [
      ...props.serviceRight.mandatoryGroups,
      ...props.serviceRight.currentMembers.filter(({ user }) => !user),
    ].map(({ id }) => id)

    currentUsers.value = props.serviceRight.currentMembers
      .filter(({ user }) => user)
      .map(({ id }) => id)
  },
)
</script>

<template>
  <v-dialog
    v-model="modelValue"
    scrollable
    :max-width="1024"
  >
    <v-card rounded="xl">
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title>
          <h1 class="mb-0">
            {{ serviceRight.description }}
          </h1>
        </v-toolbar-title>
        <template #append>
          <v-btn
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            class="me-1"
            @click="close"
          />
        </template>
      </v-toolbar>

      <v-card-text class="pt-0 py-3 manage-service-rights-dialog-container">
        <div>
          <h2>Groupes</h2>

          <fieldset>
            <legend class="sr-only">
              Groupes
            </legend>
            <ul>
              <li
                v-for="item in checkboxes"
                :key="item.id"
              >
                <input
                  :id="item.id"
                  v-model="checkedGroups"
                  :value="item.id"
                  type="checkbox"
                  :disabled="disabled.includes(item.id)"
                >
                <label :for="item.id">{{ item.displayName }}</label>
              </li>
            </ul>
          </fieldset>
        </div>

        <div v-if="serviceRight.allowPeople">
          <h2>Personnes</h2>

          <div class="field">
            <div class="field-layout">
              <div class="field-container">
                <div class="middle">
                  <label for="user">
                    Ajouter un utilisateur (Nom Prénom)
                  </label>
                  <input
                    id="user"
                    type="text"
                    placeholder=""
                  >
                </div>
              </div>
              <div class="active-indicator" />
            </div>
          </div>

          <ul>
            <li
              v-for="user in serviceRight.currentMembers.filter(({ user }) => user)"
              :key="user.id"
            >
              <span
                :style="{
                  'text-decoration': !currentUsers.includes(user.id)
                    ? 'line-through'
                    : undefined,
                }"
              >
                {{ user.displayName }}
              </span>

              <button
                class="btn-secondary small"
                @click="
                  currentUsers.includes(user.id)
                    ? removeUser(user.id)
                    : addUser(user.id)
                "
              >
                {{ currentUsers.includes(user.id) ? 'Supprimer' : 'Rétablir' }}
                <font-awesome-icon :icon="`fas fa-${currentUsers.includes(user.id) ? 'trash' : 'rotate-left'}`" />
              </button>
            </li>
          </ul>
        </div>
      </v-card-text>

      <v-card-actions>
        <button
          class="btn-secondary"
          @click="close"
        >
          Annuler
          <font-awesome-icon icon="fas fa-xmark" />
        </button>
        <button
          :disabled="!canSave"
          class="btn-primary"
          @click="save"
        >
          Enregistrer
          <font-awesome-icon icon="fas fa-floppy-disk" />
        </button>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/mixins' as *;

.manage-service-rights-dialog-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

ul {
  @include unstyled-list;
}

fieldset {
  border: none;
}
</style>
