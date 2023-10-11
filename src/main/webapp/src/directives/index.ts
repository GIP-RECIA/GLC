import { admin, role } from '@/directives/roles';
import type { App } from 'vue';

const register = (app: App) => {
  app.directive('admin', admin);
  app.directive('role', role);
};

export { register };
