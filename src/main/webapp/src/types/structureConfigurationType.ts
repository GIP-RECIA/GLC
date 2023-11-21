import type { Etat } from '@/types/enums/Etat.ts';

export type StructureConfiguration = {
  dashboard?: {};
  info?: {};
  administrativeStaff: {
    accountStates: Array<Etat>;
  };
  teachingStaff: {
    accountStates: Array<Etat>;
  };
  accounts?: {};
  exports?: {};
};
