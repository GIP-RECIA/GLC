import { createVuetify } from 'vuetify';
import { md3 } from 'vuetify/blueprints';
import { aliases, fa } from 'vuetify/iconsets/fa';

export default createVuetify({
  blueprint: md3,
  icons: {
    defaultSet: 'fa',
    aliases,
    sets: {
      fa,
    },
  },
});
