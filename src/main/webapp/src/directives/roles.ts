import { useConfigurationStore } from '@/stores/configurationStore';
import type { Directive } from 'vue';

const admin: Directive<HTMLElement, null> = (el) => {
  const configurationStore = useConfigurationStore();
  const { identity } = configurationStore;

  if (!identity?.roles.includes('ROLE_ADMIN')) el.remove();
};

const role: Directive<HTMLElement, Array<string>> = (el, binding) => {
  const configurationStore = useConfigurationStore();
  const { identity } = configurationStore;

  let hasRole: boolean = false;
  binding.value.forEach((role) => {
    if (identity?.roles.includes(role)) hasRole = true;
  });

  if (!hasRole) el.remove();
};

export { admin, role };
