/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import { library } from '@fortawesome/fontawesome-svg-core';
import { faSquare as farSquare, faUser as farUser } from '@fortawesome/free-regular-svg-icons';
import {
  faCaretDown,
  faCaretUp,
  faCheckCircle,
  faCheckSquare,
  faChevronDown,
  faChevronLeft,
  faChevronRight,
  faChevronUp,
  faCircle,
  faCircleInfo,
  faExclamation,
  faExclamationTriangle,
  faFilter,
  faFilterCircleXmark,
  faFloppyDisk,
  faHourglassEnd,
  faHourglassHalf,
  faHourglassStart,
  faLink,
  faLinkSlash,
  faMagnifyingGlass,
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

const register = (app: App): void => {
  library.add(farSquare, farUser);
  library.add(
    faCaretDown,
    faCaretUp,
    faCheckCircle,
    faCheckSquare,
    faChevronDown,
    faChevronLeft,
    faChevronRight,
    faChevronUp,
    faCircle,
    faCircleInfo,
    faExclamation,
    faExclamationTriangle,
    faFilter,
    faFilterCircleXmark,
    faFloppyDisk,
    faHourglassEnd,
    faHourglassHalf,
    faHourglassStart,
    faLink,
    faLinkSlash,
    faMagnifyingGlass,
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
