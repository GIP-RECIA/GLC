import { authenticated } from '@/directives/authenticationDirective.ts';
import { permission } from '@/directives/permissionDirective.ts';
import { admin, role } from '@/directives/roleDirective.ts';
import type { App } from 'vue';

const register = (app: App) => {
  app.directive('authenticated', authenticated);
  app.directive('permission', permission);
  app.directive('admin', admin);
  app.directive('role', role);
};

export { register };
