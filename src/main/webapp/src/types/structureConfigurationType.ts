import type { Etat } from '@/types/enums/Etat.ts';

export type StructureConfiguration = {
  dashboard: {};
  info: {};
  teachingStaff: {
    accountStates: Array<Etat>;
  };
  schoolStaff: {
    accountStates: Array<Etat>;
  };
  academicStaff: {
    accountStates: Array<Etat>;
  };
  collectivityStaff: {
    accountStates: Array<Etat>;
  };
  accounts: {};
};
