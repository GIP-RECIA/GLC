/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { useConfigurationStore } from '@/stores';
import { storeToRefs } from 'pinia';
import { type Directive, watch } from 'vue';

const authenticated: Directive<HTMLElement, null> = (el) => {
  const configurationStore = useConfigurationStore();
  const { isAuthenticated } = storeToRefs(configurationStore);

  const checkAuthentication = (): void => {
    el.hidden = !isAuthenticated.value;
  };

  watch(isAuthenticated, () => checkAuthentication(), { immediate: true });
};

export { authenticated };
