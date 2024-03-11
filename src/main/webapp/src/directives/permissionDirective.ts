import { useStructureStore } from '@/stores/structureStore.ts';
import { storeToRefs } from 'pinia';
import { type Directive, watch } from 'vue';

const permission: Directive<HTMLElement, Array<string>> = (el, binding) => {
  const structureStore = useStructureStore();
  const { currentEtab } = storeToRefs(structureStore);

  const checkPermissions = (): void => {
    let hasPermission: boolean = false;
    binding.value.forEach((permission) => {
      if (currentEtab.value?.permission?.includes(permission)) hasPermission = true;
    });

    el.hidden = !hasPermission;
  };

  watch(
    () => currentEtab.value?.permission,
    () => checkPermissions(),
    { immediate: true },
  );
};

export { permission };
