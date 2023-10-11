import { createApp } from 'vue';

import { register as registerDirectives } from '@/directives';
import { register as registerFontAwsome } from '@/plugins/fontawsome';
import '@/plugins/date-fns';
import i18n from '@/plugins/i18n';
import pinia from '@/plugins/pinia';
import vuetify from '@/plugins/vuetify';
import router from '@/router';
import Toast from 'vue-toastification';

import 'vuetify/styles';
import 'vue-toastification/dist/index.css';
import '@/assets/main.scss';

import App from '@/App.vue';

const app = createApp(App);

registerDirectives(app);
registerFontAwsome(app);

app.use(i18n);
app.use(pinia);
app.use(vuetify);
app.use(router);
app.use(Toast);

app.mount('#app');
