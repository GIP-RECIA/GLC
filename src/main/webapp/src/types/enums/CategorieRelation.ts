/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import type { enumValues } from '../enumValuesTypes'

export enum CategorieRelation {
  EnsTutStage = 'EnsTutStage',
  EnsRespStage = 'EnsRespStage',
  MAStage = 'MAStage',
  PersRel = 'PersRel',
}

export const categorieRelationMap: Record<CategorieRelation, enumValues> = {
  [CategorieRelation.EnsTutStage]: {
    i18n: 'page.user.relation.internshipSupervisor',
  },
  [CategorieRelation.EnsRespStage]: {
    i18n: 'page.user.relation.internshipCoordinator',
  },
  [CategorieRelation.MAStage]: {
    i18n: 'page.user.relation.internshipAdvisor',
  },
  [CategorieRelation.PersRel]: {
    i18n: 'page.user.relation.legalGuardian',
  },
}
