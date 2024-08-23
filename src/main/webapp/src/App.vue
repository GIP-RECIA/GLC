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
import LoginDialog from '@/components/dialogs/LoginDialog.vue';
import SettingsDialog from '@/components/dialogs/SettingsDialog.vue';
import CustomTabBar from '@/components/tab/CustomTabBar.vue';
import { useConfigurationStore } from '@/stores/index.ts';
import { watchOnce } from '@vueuse/core';
import { storeToRefs } from 'pinia';
import { onBeforeMount } from 'vue';

const configurationStore = useConfigurationStore();
const { init, initFonctions } = configurationStore;
const { configuration, isInit, isLoading, isSettings, isAuthenticated } = storeToRefs(configurationStore);

init();

watchOnce(isInit, (newValue) => {
  if (!newValue) return;

  let extendedUportalHeaderScript = document.createElement('script');
  extendedUportalHeaderScript.setAttribute('src', configuration.value!.front.extendedUportalHeader.componentPath);
  document.head.appendChild(extendedUportalHeaderScript);
  let extendedUportalFooterScript = document.createElement('script');
  extendedUportalFooterScript.setAttribute('src', configuration.value!.front.extendedUportalFooter.componentPath);
  document.head.appendChild(extendedUportalFooterScript);
});

watchOnce(isAuthenticated, (newValue) => {
  if (newValue) initFonctions();
});

onBeforeMount(() => {
  document.title = __APP_NAME__;
});

const appName = __APP_NAME__;
</script>

<template>
  <v-app class="app-container">
    <header>
      <extended-uportal-header
        v-if="isInit"
        :service-name="appName"
        :context-api-url="configuration!.front.extendedUportalHeader.contextApiUrl"
        :sign-out-url="configuration!.front.extendedUportalHeader.signOutUrl"
        :default-org-logo-path="configuration!.front.extendedUportalHeader.defaultOrgLogoPath"
        :default-avatar-path="configuration!.front.extendedUportalHeader.defaultAvatarPath"
        :default-org-icon-path="configuration!.front.extendedUportalHeader.defaultOrgIconPath"
        :favorite-api-url="configuration!.front.extendedUportalHeader.favoriteApiUrl"
        :layout-api-url="configuration!.front.extendedUportalHeader.layoutApiUrl"
        :organization-api-url="configuration!.front.extendedUportalHeader.organizationApiUrl"
        :portlet-api-url="configuration!.front.extendedUportalHeader.portletApiUrl"
        :user-info-api-url="configuration!.front.extendedUportalHeader.userInfoApiUrl"
        :user-info-portlet-url="configuration!.front.extendedUportalHeader.userInfoPortletUrl"
        :session-api-url="configuration!.front.extendedUportalHeader.sessionApiUrl"
        :template-api-path="configuration!.front.extendedUportalHeader.templateApiPath"
        :switch-org-portlet-url="configuration!.front.extendedUportalHeader.switchOrgPortletUrl"
        :favorites-portlet-card-size="configuration!.front.extendedUportalHeader.favoritesPortletCardSize"
        :grid-portlet-card-size="configuration!.front.extendedUportalHeader.gridPortletCardSize"
        :hide-action-mode="configuration!.front.extendedUportalHeader.hideActionMode"
        :show-favorites-in-slider="configuration!.front.extendedUportalHeader.showFavoritesInSlider"
        :return-home-title="configuration!.front.extendedUportalHeader.returnHomeTitle"
        :return-home-target="configuration!.front.extendedUportalHeader.returnHomeTarget"
        :icon-type="configuration!.front.extendedUportalHeader.iconType"
      />
      <v-toolbar v-if="isAuthenticated" density="compact" color="rgba(255, 255, 255, 0)">
        <v-progress-linear :active="isLoading" :indeterminate="isLoading" absolute bottom color="primary" />
        <div class="d-flex align-center w-100 px-1">
          <custom-tab-bar />
          <v-spacer />
          <v-btn
            icon="fas fa-gear"
            variant="text"
            color="default"
            size="small"
            class="ms-1 text-medium-emphasis"
            @click="isSettings = true"
          />
        </div>
      </v-toolbar>
    </header>
    <div class="d-flex flex-column h-100 overflow-y-auto">
      <v-main class="flex-grow-1">
        <router-view v-if="isAuthenticated" />
        <login-dialog />
        <settings-dialog />
      </v-main>
      <footer>
        <extended-uportal-footer
          v-if="isInit"
          :template-api-path="configuration!.front.extendedUportalFooter.templateApiPath"
        />
      </footer>
    </div>
  </v-app>
</template>
