import additionals from './additionals.json';
import alerts from './alerts.json';
import buttons from './buttons.json';
import main from './main.json';
import offices from './offices.json';
import persons from './persons.json';
import searchs from './searchs.json';
import sources from './sources.json';
import tabs from './tabs.json';
import toasts from './toasts.json';
import { fr } from 'vuetify/locale';

export default {
  ...additionals,
  ...alerts,
  ...buttons,
  ...main,
  ...offices,
  ...persons,
  ...searchs,
  ...sources,
  ...tabs,
  ...toasts,
  $vuetify: {
    ...fr,
  },
};
