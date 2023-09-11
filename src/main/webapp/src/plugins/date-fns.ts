import { setDefaultOptions } from 'date-fns';
import { fr } from 'date-fns/locale';

if (window.navigator.language == 'fr' || window.navigator.language == 'fr-FR') {
  setDefaultOptions({ locale: fr });
}
