import type { SimplePersonne } from '@/types/personneType';

export type Discipline = {
  id: number;
  code: string;
  disciplinePoste: string;
  source: string;
  personnes: Array<SimplePersonne>;
};
