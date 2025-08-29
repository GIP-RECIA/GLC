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
import { useLocalStorage, usePreferredDark } from '@vueuse/core'
import { storeToRefs } from 'pinia'
import { computed, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useTheme } from 'vuetify'
import { useConfigurationStore } from '@/stores'
import { Theme } from '@/types/enums'

const configurationStore = useConfigurationStore()
const { isSettings } = storeToRefs(configurationStore)

const { t } = useI18n()
const isDark = usePreferredDark()
const theme = useTheme()

const modelValue = computed<boolean>({
  get() {
    return isSettings.value
  },
  set() {},
})

const selectedTheme = useLocalStorage<Array<Theme>>(`${__APP_SLUG__}.theme`, [Theme.system])

watch(
  [selectedTheme, isDark],
  ([newAppearanceTheme, newIsDark]) => {
    let selected = newIsDark ? Theme.dark : Theme.light
    switch (newAppearanceTheme[0]) {
      case Theme.light:
        selected = Theme.light
        break
      case Theme.dark:
        selected = Theme.dark
        break
    }
    theme.global.name.value = selected
  },
  { immediate: true },
)

function onClose(): void {
  isSettings.value = false
}
</script>

<template>
  <v-dialog v-model="modelValue" :max-width="1024">
    <v-card rounded="xl">
      <v-toolbar :title="t('settings.title')" color="rgba(255, 255, 255, 0)">
        <template #append>
          <v-btn
            icon="fas fa-xmark"
            color="default"
            variant="plain"
            :alt="t('button.close')"
            class="me-1"
            @click="onClose"
          />
        </template>
      </v-toolbar>
      <v-list v-model:selected="selectedTheme" mandatory class="py-0">
        <v-list-subheader>{{ t('settings.theme.subheader') }}</v-list-subheader>
        <v-list-item v-for="th in Theme" :key="th" :title="t(`settings.theme.${th}`)" :value="th">
          <template #prepend="{ isActive }">
            <v-list-item-action start>
              <v-radio :model-value="isActive" />
            </v-list-item-action>
          </template>
        </v-list-item>
      </v-list>
    </v-card>
  </v-dialog>
</template>
