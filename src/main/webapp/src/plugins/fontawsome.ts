import { library } from '@fortawesome/fontawesome-svg-core';
import { far } from '@fortawesome/free-regular-svg-icons';
import { fas } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import type { App } from 'vue';

const register = (app: App) => {
  library.add(far, fas);
  app.component('font-awesome-icon', FontAwesomeIcon);
};

export { register };
