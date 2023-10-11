<script setup lang="ts">
import { useConfigurationStore } from '@/stores/configurationStore';
import { intercept } from '@/utils/axiosUtils';
import { login } from '@/utils/casUtils';
import { watchOnce } from '@vueuse/core';
import { storeToRefs } from 'pinia';
import { computed } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { isInit, isAuthenticated } = storeToRefs(configurationStore);

const { t } = useI18n();

const modelValue = computed<boolean>({
  get() {
    return !isAuthenticated.value;
  },
  set() {},
});

watchOnce(isInit, (newValue) => {
  if (newValue && !isAuthenticated.value) {
    login();
    intercept();
  }
});
</script>

<template>
  <v-dialog v-model="modelValue" :max-width="300">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title :text="t('casSignIn')" class="text-h6" />
      </v-toolbar>
      <v-card-text />
      <v-card-actions>
        <v-spacer />
        <v-btn
          id="signIn"
          color="primary"
          prepend-icon="fas fa-right-to-bracket"
          :text="t('button.signIn')"
          :disabled="!isInit"
          @click="login"
        />
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
