export interface ServiceRights {
  service: string
  rights: ServiceRight[]
}

export interface ServiceRight {
  role: string
  isAdmin: boolean
  description: string
  currentMembers: RightMember[]
  possibleGroups: RightMember[]
  mandatoryGroups: RightMember[]
  allowPeople: boolean
}

export interface RightMember {
  id: string
  displayName: string
  user: boolean
  external: boolean
}
