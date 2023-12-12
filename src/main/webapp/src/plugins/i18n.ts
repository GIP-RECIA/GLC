import en from '@/locales/en/index.ts';
import fr from '@/locales/fr/index.ts';
import { createI18n } from 'vue-i18n';

export default createI18n({
  legacy: false,
  allowComposition: true,
  locale: window.navigator.language,
  fallbackLocale: 'fr',
  messages: {
    en,
    'en-US': en,
    fr,
    'fr-FR': fr,
  },
});
