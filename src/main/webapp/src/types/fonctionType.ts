import type { Discipline } from '@/types/disciplineType.ts';
import type { Filiere } from '@/types/filiereType.ts';

export type SourceFonction = {
  source: string;
  filieres: Array<Filiere>;
  customMapping?: CustomMapping;
};

export type CustomMapping = {
  filieres: Array<Filiere>;
  disciplines: Array<Discipline>;
};

export type PersonneFonction = {
  disciplinePoste: number;
  filiere: number;
  source: string;
  structure: number;
  dateFin?: string;
};
