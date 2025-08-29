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

import { acceptHMRUpdate, createPinia } from 'pinia'
import { useConfigurationStore, usePersonneStore, useStructureStore } from '@/stores'

const { hot } = import.meta

if (hot) {
  hot.accept(acceptHMRUpdate(useConfigurationStore, hot))
  hot.accept(acceptHMRUpdate(usePersonneStore, hot))
  hot.accept(acceptHMRUpdate(useStructureStore, hot))
}

export default createPinia()
