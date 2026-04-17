import type { CategoriePersonne, Etat } from './enums/index.ts'
import type { UserStructure } from './structureTypes.ts'

export interface CommonUser {
  id: number
  etat: Etat
  local: boolean
}

export type FunctionUser = CommonUser & {
  cn: string
  dateSuppression: string
}

export type RelationUser = FunctionUser

export type SearchUser = CommonUser

export type AccountUser = CommonUser & {
  nom: string
  prenom: string
  uid?: string
  categoriePersonne: CategoriePersonne
  login: string
  guichet?: string
  email?: string
  dateModification: string
  dateSuppression?: string
}

export type User = CommonUser & {
  anneeScolaire: string
  categorie: CategoriePersonne
  civilite: string
  source: string
  dateNaissance: string
  email?: string
  givenName: string
  patronyme: string
  cn: string
  sn: string
  uid?: string
  dateFin?: string
  dateSourceModification: string
  login: string
  guichet?: string
  dateModification?: string
  dateAcquittement?: string
  dateSuppression?: string
  photo?: string
  idPronote?: string
  listeRouge: boolean
  listeStructures: UserStructure[]
  relations: UserRelation[]
}

export interface UserRelation {
  categorieRelation: string
  personneEnRelation: RelationUser
  holder: boolean
}
