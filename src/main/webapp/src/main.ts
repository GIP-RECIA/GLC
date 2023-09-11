import { createApp } from 'vue';

import '@/plugins/date-fns';
// @ts-ignore
// import '@/plugins/fontawsome';
import i18n from '@/plugins/i18n';
import pinia from '@/plugins/pinia';
import vuetify from '@/plugins/vuetify';
import router from '@/router';

import Toast from 'vue-toastification';
// @ts-ignore
// import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';

import 'vuetify/styles';
import '@fortawesome/fontawesome-free/css/all.css';
import 'vue-toastification/dist/index.css';
import '@/assets/main.scss';

import App from '@/App.vue';

const app = createApp(App);

// app.component('font-awesome-icon', FontAwesomeIcon)

app.use(pinia);
app.use(router);
app.use(vuetify);
app.use(i18n);
app.use(Toast);

app.mount('#app');
