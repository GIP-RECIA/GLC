export interface StructureRestriction {
  dateRentreeEtab: string | null
  niveaux: LevelRestriction[]
}

export interface LevelRestriction {
  niveau: string
  dateRentreeNiveau: string | null
  classes: ClassRestriction[]
}

export interface ClassRestriction {
  classe: string
  dateRentreeClasse: string | null
}
