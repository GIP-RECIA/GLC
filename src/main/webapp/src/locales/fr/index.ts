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
import additionals from './additionals.json';
import alerts from './alerts.json';
import buttons from './buttons.json';
import main from './main.json';
import offices from './offices.json';
import persons from './persons.json';
import searchs from './searchs.json';
import sources from './sources.json';
import tabs from './tabs.json';
import toasts from './toasts.json';
import { fr } from 'vuetify/locale';

export default {
  ...additionals,
  ...alerts,
  ...buttons,
  ...main,
  ...offices,
  ...persons,
  ...searchs,
  ...sources,
  ...tabs,
  ...toasts,
  $vuetify: {
    ...fr,
  },
};
