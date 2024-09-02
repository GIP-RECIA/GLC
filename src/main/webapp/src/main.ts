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

import { createApp } from 'vue';

import { register as registerDirectives } from '@/directives';
import { register as registerFontAwsome } from '@/plugins/fontawsome.ts';
import '@/plugins/date-fns.ts';
import i18n from '@/plugins/i18n.ts';
import pinia from '@/plugins/pinia.ts';
import vuetify from '@/plugins/vuetify.ts';
import router from '@/router';
import Vue3Toasity, { type ToastContainerOptions } from 'vue3-toastify';

import 'vuetify/styles';
import 'vue3-toastify/dist/index.css';
import '@/assets/main.scss';

import App from '@/App.vue';

const app = createApp(App);

registerDirectives(app);
registerFontAwsome(app);

app.use(i18n);
app.use(pinia);
app.use(vuetify);
app.use(router);
app.use(Vue3Toasity, { limit: 0, newestOnTop: true, theme: 'colored' } as ToastContainerOptions);

app.mount('#app');

console.log("Version", __BACK_VERSION__);
