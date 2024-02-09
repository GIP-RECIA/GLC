import type { endInfo } from '@/types/endInfoType.ts';
import type { SimplePersonne } from '@/types/personneType.ts';

export type Discipline = {
  id: number;
  code: string;
  disciplinePoste: string;
  source: string;
  personnes: Array<SimplePersonne>;
  endInfo?: endInfo;
};
