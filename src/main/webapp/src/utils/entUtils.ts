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

import type { PortalEntry } from '@/types'
import vuetify from '@/plugins/vuetify.ts'
import axios from 'axios'

async function useEntTheme(templateApiPath: string | undefined): Promise<void> {
  if (!templateApiPath || templateApiPath.trim().length < 1)
    return
  const response = await axios.get(templateApiPath)
  const domainInfo: PortalEntry = response.data.data.find((domain: PortalEntry) =>
    domain.identity.domains?.includes(window.location.hostname),
  )

  const primary: string | undefined = domainInfo?.colors.find(color => color.Id === 'primary')?.hexa
  if (!primary)
    return
  for (const prop in vuetify.theme.themes.value) {
    vuetify.theme.themes.value[prop].colors.primary = primary
  }
}

export { useEntTheme }
