import { library } from '@fortawesome/fontawesome-svg-core';
import { faSquare as farSquare, faUser as farUser } from '@fortawesome/free-regular-svg-icons';
import {
  faCaretDown,
  faCaretUp,
  faCheckSquare,
  faChevronDown,
  faChevronLeft,
  faChevronRight,
  faChevronUp,
  faCircle,
  faCircleInfo,
  faFilter,
  faFilterCircleXmark,
  faFloppyDisk,
  faLink,
  faLinkSlash,
  faMoon,
  faPen,
  faPlus,
  faRightToBracket,
  faSearch,
  faSun,
  faTimesCircle,
  faUser,
  faUserClock,
  faUserLock,
  faUserPen,
  faUserSecret,
  faXmark,
} from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome';
import type { App } from 'vue';

const register = (app: App) => {
  library.add(farSquare, farUser);
  library.add(
    faCaretDown,
    faCaretUp,
    faCheckSquare,
    faChevronDown,
    faChevronLeft,
    faChevronRight,
    faChevronUp,
    faCircle,
    faCircleInfo,
    faFilter,
    faFilterCircleXmark,
    faFloppyDisk,
    faLink,
    faLinkSlash,
    faMoon,
    faPen,
    faPlus,
    faRightToBracket,
    faSearch,
    faSun,
    faTimesCircle,
    faUser,
    faUserClock,
    faUserLock,
    faUserPen,
    faUserSecret,
    faXmark,
  );
  app.component('font-awesome-icon', FontAwesomeIcon);
};

export { register };
