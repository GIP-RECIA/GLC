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

import type { App } from 'vue'
import { authenticated } from './authenticationDirective.ts'
import { permission } from './permissionDirective.ts'
import { admin, role } from './roleDirective.ts'

function register(app: App): void {
  app.directive('authenticated', authenticated)
  app.directive('permission', permission)
  app.directive('admin', admin)
  app.directive('role', role)
}

export { register }
