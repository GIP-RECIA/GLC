import type { Discipline } from '@/types/disciplineType.ts';

export type Filiere = {
  id: number;
  codeFiliere: string;
  libelleFiliere: string;
  source: string;
  disciplines: Array<Discipline>;
};
