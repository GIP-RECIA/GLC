import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { storeToRefs } from 'pinia';
import { type Directive, watch } from 'vue';

const authenticated: Directive<HTMLElement, null> = (el) => {
  const configurationStore = useConfigurationStore();
  const { isAuthenticated } = storeToRefs(configurationStore);

  const checkAuthentication = (): void => {
    el.hidden = !isAuthenticated.value;
  };

  watch(isAuthenticated, () => checkAuthentication(), { immediate: true });
};

export { authenticated };
