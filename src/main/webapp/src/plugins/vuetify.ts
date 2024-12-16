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

import { Theme } from '@/types/enums'
import DateFnsAdapter from '@date-io/date-fns'
import { enUS, fr } from 'date-fns/locale'
import { useI18n } from 'vue-i18n'
import { createVuetify, type ThemeDefinition } from 'vuetify'
import { md3 } from 'vuetify/blueprints'
import { aliases, fa } from 'vuetify/iconsets/fa-svg'
import { createVueI18nAdapter } from 'vuetify/locale/adapters/vue-i18n'
import i18n from './i18n.ts'

const themes: Record<string, ThemeDefinition> = {
  [Theme.light]: {
    dark: false,
    colors: {
      background: '#eeeeee',
    },
  },
  [Theme.dark]: {
    dark: true,
  },
}

export default createVuetify({
  blueprint: md3,
  icons: {
    defaultSet: 'fa',
    aliases,
    sets: {
      fa,
    },
  },
  theme: {
    defaultTheme: Theme.light,
    themes,
  },
  locale: {
    adapter: createVueI18nAdapter({ i18n, useI18n }),
  },
  date: {
    adapter: DateFnsAdapter,
    locale: {
      fr,
      en: enUS,
    },
  },
})
