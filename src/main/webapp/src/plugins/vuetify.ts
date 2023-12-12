import i18n from '@/plugins/i18n.ts';
import { useI18n } from 'vue-i18n';
import { type ThemeDefinition, createVuetify } from 'vuetify';
import { md3 } from 'vuetify/blueprints';
import { aliases, fa } from 'vuetify/iconsets/fa-svg';
import { createVueI18nAdapter } from 'vuetify/locale/adapters/vue-i18n';

const light: ThemeDefinition = {
  dark: false,
  colors: {
    background: '#eeeeee',
  },
};

const dark: ThemeDefinition = {
  dark: true,
};

export default createVuetify({
  blueprint: md3,
  icons: {
    defaultSet: 'fa',
    aliases,
    sets: {
      fa,
    },
  },
  theme: {
    defaultTheme: 'light',
    themes: {
      light,
      dark,
    },
  },
  locale: {
    adapter: createVueI18nAdapter({ i18n, useI18n }),
  },
});
