import type { Alert } from './alertTypes.ts'
import type { Composition, UserFunction } from './functionTypes.ts'
import type { AccountUser, FunctionUser } from './userTypes.ts'

export interface CommonStructure {
  id: number
  type?: string
  uai?: string
  siren: string
  nom: string
}

export type SearchStructure = CommonStructure & {
  ville?: string
}

export type UserStructure = CommonStructure & {
  structureRattachement: boolean
  structureCourante: boolean
  fonctions: UserFunction[]
  additionalFonctions: UserFunction[]
  classes: string[]
  groupesPedagogiques: string[]
  authorizedForPrincipal: boolean
}

export type Structure = CommonStructure & {
  nomCourt?: string
  etat: string
  etatAlim: string
  source: string
  anneeScolaire: string
  adresse: {
    adresse: string
    codePostal: string
    ville: string
    boitePostale: string
    pays: string
  }
  categorie: string
  mail: string
  siteWeb: string
  modeleLogin: string
  logo: string
  personnes: AccountUser[]
  filieres: Composition[]
  withoutFunctions?: FunctionUser[]
  alerts: Alert[]
  permission?: string
}
