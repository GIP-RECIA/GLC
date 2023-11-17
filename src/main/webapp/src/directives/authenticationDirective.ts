import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { storeToRefs } from 'pinia';
import { type Directive, watch } from 'vue';

const authenticated: Directive<HTMLElement, null> = (el) => {
  const configurationStore = useConfigurationStore();
  const { isAuthenticated } = storeToRefs(configurationStore);

  const checkAuthentication = () => {
    el.hidden = !isAuthenticated.value;
  };

  checkAuthentication();

  watch(isAuthenticated, (oldValue, newValue) => {
    if (oldValue != newValue) checkAuthentication();
  });
};

export { authenticated };
