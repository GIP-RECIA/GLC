import { useConfigurationStore } from '@/stores/configurationStore.ts';
import { storeToRefs } from 'pinia';
import { type Directive, watch } from 'vue';

const admin: Directive<HTMLElement, null> = (el) => {
  const configurationStore = useConfigurationStore();
  const { identity } = storeToRefs(configurationStore);

  const checkAdmin = (): void => {
    let isAdmin: boolean = false;
    if (identity.value?.roles.includes('ROLE_ADMIN')) isAdmin = true;

    el.hidden = !isAdmin;
  };

  watch(
    () => identity.value?.roles,
    () => checkAdmin(),
    { immediate: true, deep: true },
  );
};

const role: Directive<HTMLElement, Array<string>> = (el, binding) => {
  const configurationStore = useConfigurationStore();
  const { identity } = storeToRefs(configurationStore);

  const checkRoles = (): void => {
    let hasRole: boolean = false;
    binding.value.forEach((role) => {
      if (identity.value?.roles.includes(role)) hasRole = true;
    });

    el.hidden = !hasRole;
  };

  watch(
    () => identity.value?.roles,
    () => checkRoles(),
    { immediate: true, deep: true },
  );
};

export { admin, role };
