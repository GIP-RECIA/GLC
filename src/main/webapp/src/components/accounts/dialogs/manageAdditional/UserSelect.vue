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
import type { SearchUser } from '@/types/index.ts'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { format } from 'date-fns'
import { h } from 'vue'
import { useI18n } from 'vue-i18n'
import { etatMap } from '@/types/enums/index.ts'
import { concatenate, getIconDefinition, getStateLabel } from '@/utils/index.ts'

defineProps<{
  users?: SearchUser[]
}>()

const modelValue = defineModel<SearchUser | undefined>()

const modelValueSearch = defineModel<string | undefined>('search')

const { t } = useI18n()

function renderEtat(user: SearchUser) {
  const etat = {
    icon: getIconDefinition(user.local),
    ...etatMap[user.etat],
  }
  const suppressDate = user.dateSuppression
    ? format(user.dateSuppression, 'P')
    : undefined
  const title = getStateLabel(
    etat.i18n,
    suppressDate,
    t,
  )

  return h(
    'span',
    {
      title,
    },
    [
      h(FontAwesomeIcon, {
        icon: etat.icon,
        size: 'lg',
        style: {
          color: etat.color,
        },
      }),
    ],
  )
}
</script>

<template>
  <v-autocomplete
    v-model="modelValue"
    v-model:search="modelValueSearch"
    :items="users"
    item-value="id"
    item-title="cn"
    :filter-keys="['raw.cn', 'raw.email', 'raw.uid']"
    variant="solo-filled"
    hide-details
    return-object
    flat
  >
    <template #item="{ props: itemProps, item }">
      <v-list-item
        v-bind="itemProps"
        :subtitle="
          concatenate(
            [item.raw.email, item.raw.uid],
            ' ',
          )
        "
      >
        <template #title>
          {{ item.raw.cn }}
        </template>
        <template #prepend>
          <component :is="renderEtat(item.raw)" />
        </template>
      </v-list-item>
    </template>
  </v-autocomplete>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
