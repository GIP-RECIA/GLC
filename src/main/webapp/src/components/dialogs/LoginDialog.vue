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
import { useConfigurationStore } from '@/stores/index.ts';
import { intercept } from '@/utils/axiosUtils.ts';
import { login } from '@/utils/casUtils.ts';
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
    <v-card rounded="xl">
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
