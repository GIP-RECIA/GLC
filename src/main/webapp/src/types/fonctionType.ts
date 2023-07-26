import type { Discipline } from "@/types/disciplineType";
import type { Filiere } from "@/types/filiereType";
import { type } from "os";

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
};
