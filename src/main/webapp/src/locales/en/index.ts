import additionals from './additionals.json';
import alerts from './alerts.json';
import buttons from './buttons.json';
import main from './main.json';
import persons from './persons.json';
import searchs from './searchs.json';
import tabs from './tabs.json';
import toasts from './toasts.json';
import { en } from 'vuetify/locale';

export default {
  ...additionals,
  ...alerts,
  ...buttons,
  ...main,
  ...persons,
  ...searchs,
  ...tabs,
  ...toasts,
  $vuetify: {
    ...en,
  },
};
