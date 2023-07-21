import { defineStore } from "pinia";
import { ref } from "vue";

export const useCasPublisherStore = defineStore("casPublisherStore", () => {
  const loginModalOpened = ref(null);
  const previousRoute = ref({ params: {}, meta: {} });
  const nextRoute = ref({ params: {}, meta: {} });
  const returnRoute = ref(null);
  const lang = ref("fr");
  const identity = ref(null);
  const authenticated = ref(false);

  const clearAll = () => {
    identity.value = null;
    authenticated.value = false;
  };

  return {
    loginModalOpened,
    previousRoute,
    nextRoute,
    returnRoute,
    lang,
    identity,
    authenticated,
    clearAll,
  };
});
