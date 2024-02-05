import { createApp } from 'vue';

import { register as registerDirectives } from '@/directives/index.ts';
import { register as registerFontAwsome } from '@/plugins/fontawsome.ts';
import '@/plugins/date-fns.ts';
import i18n from '@/plugins/i18n.ts';
import pinia from '@/plugins/pinia.ts';
import vuetify from '@/plugins/vuetify.ts';
import router from '@/router/index.ts';
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
