import { useStructureStore } from '@/stores/structureStore';
import { storeToRefs } from 'pinia';
import { type Directive, watch } from 'vue';

const permission: Directive<HTMLElement, Array<string>> = (el, binding) => {
  const structureStore = useStructureStore();
  const { currentEtab } = storeToRefs(structureStore);

  const checkPermissions = () => {
    let hasPermission: boolean = false;
    binding.value.forEach((permission) => {
      if (currentEtab.value?.permission?.includes(permission)) hasPermission = true;
    });

    el.hidden = !hasPermission;
  };

  checkPermissions();

  watch(
    () => currentEtab.value?.permission,
    (oldValue, newValue) => {
      if (newValue != oldValue) checkPermissions();
    },
  );
};

export { permission };
