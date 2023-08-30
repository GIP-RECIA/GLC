<script setup lang="ts">
import { useConfigurationStore } from "@/stores/configurationStore";
import { jsonp } from "@/utils/casUtils";
import { storeToRefs } from "pinia";
import { onMounted, computed } from "vue";
import { useI18n } from "vue-i18n";
import { useRouter } from "vue-router";

const configurationStore = useConfigurationStore();
const { identity, isAuthenticated } = storeToRefs(configurationStore);

const { t } = useI18n();

const modelValue = computed<boolean>({
  get() {
    return !isAuthenticated.value;
  },
  set() {},
});

const { VITE_API_URL } = import.meta.env;

// Objet en charge de la redirection vers le serveur CAS
let relogState = {};

// Méthode en charge du processus de connexion
// Une fois connecté, l'utilisateur est redirigé
const login = async () => {
  try {
    const response = await jsonp("/app/login", "JSON_CALLBACK", 1000);
    // @ts-ignore
    identity.value = response;
  } catch (e) {
    identity.value = undefined;
    relog();
  }
};

// Méthode effectuant une redirection sur le serveur CAS,
// un listener est mis en place afin de détecter la réponse
// du serveur CAS
const relog = () => {
  windowOpenCleanup(relogState);
  // @ts-ignore
  relogState.listener = onmessage;
  window.addEventListener("message", onmessage);

  // @ts-ignore
  relogState.window = window.open(`${VITE_API_URL}/app/login?postMessage`);
};

// Méthode de nettoyage de la page de login
// @ts-ignore
const windowOpenCleanup = (state) => {
  try {
    if (state.listener) {
      window.removeEventListener("message", state.listener);
    }
    if (state.window) {
      state.window.close();
    }
  } catch (e) {
    // eslint-disable-next-line
    console.error(e);
  }
};

// Méthode utilisé lors de la réception de la réponse
// du serveur CAS puis redirige l'utilisateur
// @ts-ignore
const onmessage = (e) => {
  if (typeof e.data !== "string") {
    return;
  }

  const m = e.data.match(/^loggedUser=(.*)$/);
  if (!m) {
    return;
  }

  windowOpenCleanup(relogState);
  login();
};

const router = useRouter();

router.beforeEach(async (to, from, next) => {
  try {
    await login();
    next();
  } catch (e) {
    console.error("Login required");
  }
});

onMounted(() => {
  if (!isAuthenticated) {
    login();
  }
});
</script>

<template>
  <v-dialog v-model="modelValue" :max-width="300">
    <v-card>
      <v-toolbar color="rgba(255, 255, 255, 0)">
        <v-toolbar-title class="text-h6">{{ t("casSignIn") }}</v-toolbar-title>
      </v-toolbar>
      <v-card-text> </v-card-text>
      <v-card-actions class="d-flex justify-end">
        <v-btn
          color="primary"
          prepend-icon="fas fa-right-to-bracket"
          @click="login"
        >
          {{ t("signIn") }}
        </v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>
