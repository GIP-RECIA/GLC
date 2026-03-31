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
import { PiniaColadaDevtools } from '@pinia/colada-devtools'
import { watchOnce } from '@vueuse/core'
import { storeToRefs } from 'pinia'
import { computed, onBeforeMount } from 'vue'
import { useRoute } from 'vue-router'
import AccountToolbar from '@/components/accounts/toolbar/AccountToolbar.vue'
import { useConfigurationStore } from '@/stores/index.ts'

const route = useRoute()

const configurationStore = useConfigurationStore()
const { init, initFonctions } = configurationStore
const {
  configuration,
  isInit,
} = storeToRefs(configurationStore)

init()
initFonctions()

watchOnce(isInit, (newValue) => {
  if (!newValue || !configuration.value?.front.extendedUportal)
    return
  const { header, footer } = configuration.value.front.extendedUportal
  if (header) {
    const extendedUportalHeaderScript = document.createElement('script')
    extendedUportalHeaderScript.setAttribute('src', header.componentPath)
    document.head.appendChild(extendedUportalHeaderScript)
  }
  if (footer) {
    const extendedUportalFooterScript = document.createElement('script')
    extendedUportalFooterScript.setAttribute('src', footer.componentPath)
    document.head.appendChild(extendedUportalFooterScript)
  }
})

onBeforeMount(() => {
  document.title = __APP_NAME__
})

const isAccountSection = computed(() => (
  route.matched.some(r => r.name === 'accountRoot')
))

const appName = __APP_NAME__
</script>

<template>
  <v-app class="app-container">
    <header>
      <extended-uportal-header
        v-if="isInit"
        :service-name="appName"
        v-bind="configuration!.front.extendedUportal?.header?.props"
        navigation-drawer-visible
      />
      <AccountToolbar
        v-if="isAccountSection"
      />
    </header>
    <div class="d-flex flex-column h-100 overflow-y-auto">
      <v-main class="flex-grow-1">
        <router-view />
      </v-main>
      <footer>
        <extended-uportal-footer
          v-if="isInit"
          v-bind="configuration!.front.extendedUportal?.footer?.props"
        />
      </footer>
    </div>
  </v-app>

  <PiniaColadaDevtools />
</template>

<style lang="scss">
extended-uportal-header {
  display: block;
  height: var(--recia-header-height);
}
</style>
