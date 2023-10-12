import { authenticated } from '@/directives/authenticationDirective';
import { permission } from '@/directives/permissionDirective';
import { admin, role } from '@/directives/roleDirective';
import type { App } from 'vue';

const register = (app: App) => {
  app.directive('authenticated', authenticated);
  app.directive('permission', permission);
  app.directive('admin', admin);
  app.directive('role', role);
};

export { register };
