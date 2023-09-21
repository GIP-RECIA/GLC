import { type ThemeDefinition, createVuetify } from 'vuetify';
import { md3 } from 'vuetify/blueprints';
import { aliases, fa } from 'vuetify/iconsets/fa';

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
});
