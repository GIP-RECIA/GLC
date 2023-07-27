import "regenerator-runtime/runtime.js";

import "vuetify/styles";
import "@/assets/main.scss";

import "@/utils/momentUtils";

import App from "@/App.vue";

import i18n from "@/plugins/i18n";
import pinia from "@/plugins/pinia";
import vuetify from "@/plugins/vuetify";
import router from "@/router";

import Toast from "vue-toastification";
import "vue-toastification/dist/index.css";

import { createApp } from "vue";

const app = createApp(App);

app.use(pinia);
app.use(router);
app.use(vuetify);
app.use(i18n);
app.use(Toast);

app.mount("#app");
