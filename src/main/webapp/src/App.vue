<script setup lang="ts">
import { useFonctionStore } from './stores/fonctionStore.ts';
import ThemeSwitcher from '@/components/ThemeSwitcher.vue';
import LoginDialog from '@/components/dialogs/LoginDialog.vue';
import CustomTabBar from '@/components/tab/CustomTabBar.vue';
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { storeToRefs } from 'pinia';
import { onBeforeMount, watch } from 'vue';

const configurationStore = useConfigurationStore();
configurationStore.init();
const { isLoading, isAuthenticated } = storeToRefs(configurationStore);

const fonctionStore = useFonctionStore();
isAuthenticated.value && fonctionStore.init();

onBeforeMount(() => {
  document.title = __APP_NAME__;

  let extendedUportalHeaderScript = document.createElement('script');
  extendedUportalHeaderScript.setAttribute('src', '/commun/extended-uportal-header.min.js');
  document.head.appendChild(extendedUportalHeaderScript);
  let extendedUportalFooterScript = document.createElement('script');
  extendedUportalFooterScript.setAttribute('src', '/commun/extended-uportal-footer.min.js');
  document.head.appendChild(extendedUportalFooterScript);
});

watch(isAuthenticated, (newValue) => {
  newValue && fonctionStore.init();
});

const appName = __APP_NAME__;
const domain = window.location.hostname;
</script>

<template>
  <v-app class="app-container">
    <header v-if="isAuthenticated">
      <extended-uportal-header
        :domain="domain"
        :service-name="appName"
        context-api-url="/portail"
        sign-out-url="/portail/Logout"
        default-org-logo-path="/annuaire_images/default_banner_v1.jpg"
        default-avatar-path="/images/icones/noPictureUser.svg"
        default-org-icon-path="/images/partners/netocentre-simple.svg"
        favorite-api-url="/portail/api/layout"
        layout-api-url="/portail/api/v4-3/dlm/layout.json"
        organization-api-url="/change-etablissement/rest/v2/structures/structs/"
        portlet-api-url="/portail/api/v4-3/dlm/portletRegistry.json?category=All%20categories"
        user-info-api-url="/portail/api/v5-1/userinfo?claims=private,name,ESCOSIRENCourant,ESCOSIREN&groups="
        user-info-portlet-url="/portail/p/ESCO-MCE"
        session-api-url="/portail/api/session.json"
        template-api-path="/commun/portal_template_api.tpl.json"
        switch-org-portlet-url="/portail/p/etablissement-swapper"
        favorites-portlet-card-size="small"
        grid-portlet-card-size="auto"
        hide-action-mode="never"
        show-favorites-in-slider="true"
        return-home-title="Aller à l'accueil"
        return-home-target="_self"
        icon-type="nine-square"
      />
      <v-toolbar density="compact" color="rgba(255, 255, 255, 0)">
        <v-progress-linear :active="isLoading" :indeterminate="isLoading" absolute bottom color="primary" />
        <div class="d-flex align-center w-100 px-1">
          <custom-tab-bar />
          <v-spacer />
          <theme-switcher />
        </div>
      </v-toolbar>
    </header>
    <div class="d-flex flex-column h-100 overflow-y-auto">
      <v-main class="flex-grow-1">
        <router-view v-if="isAuthenticated" />
        <login-dialog />
      </v-main>
      <footer v-if="isAuthenticated">
        <extended-uportal-footer :domain="domain" template-api-path="/commun/portal_template_api.tpl.json" />
      </footer>
    </div>
  </v-app>
</template>
