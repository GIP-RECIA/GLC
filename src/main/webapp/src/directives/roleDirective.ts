import { useConfigurationStore } from '@/stores/configurationStore';
import { storeToRefs } from 'pinia';
import { type Directive, watch } from 'vue';

const admin: Directive<HTMLElement, null> = (el) => {
  const configurationStore = useConfigurationStore();
  const { identity } = storeToRefs(configurationStore);

  const checkAdmin = () => {
    let isAdmin: boolean = false;
    if (identity.value?.roles.includes('ROLE_ADMIN')) isAdmin = true;

    el.hidden = !isAdmin;
  };

  checkAdmin();

  watch(
    () => identity.value?.roles,
    () => checkAdmin(),
    { deep: true },
  );
};

const role: Directive<HTMLElement, Array<string>> = (el, binding) => {
  const configurationStore = useConfigurationStore();
  const { identity } = storeToRefs(configurationStore);

  const checkRoles = () => {
    let hasRole: boolean = false;
    binding.value.forEach((role) => {
      if (identity.value?.roles.includes(role)) hasRole = true;
    });

    el.hidden = !hasRole;
  };

  checkRoles();

  watch(
    () => identity.value?.roles,
    () => checkRoles(),
    { deep: true },
  );
};

export { admin, role };
