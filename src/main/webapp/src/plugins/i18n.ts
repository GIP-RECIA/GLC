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
import * as messages from '@/locales';
import { createI18n } from 'vue-i18n';

const config: {
  fallbackLocale: string;
  supportLocales: Array<string>;
} = {
  fallbackLocale: 'fr',
  supportLocales: ['en', 'fr'],
};

const findLanguage = (): string => {
  if (config.supportLocales.includes(window.navigator.language)) return window.navigator.language;

  const matchLanguages = window.navigator.languages.filter((lang) =>
    config.supportLocales.find((available) => lang == available),
  );
  if (matchLanguages.length > 0) return matchLanguages[0];

  return config.fallbackLocale;
};

export default createI18n({
  legacy: false,
  locale: findLanguage(),
  fallbackLocale: config.fallbackLocale,
  messages,
});
