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

import type { FunctionForm } from './functionTypes.ts'

export interface SetPersonneAdditionalParams {
  id: number
  structureId: number
  toAddFunctions: FunctionForm[]
  toDeleteFunctions: FunctionForm[]
  requiredAction: string
}

export type SetPersonneAdditionalWithIdParams = Omit<
  SetPersonneAdditionalParams,
  'toAddFunctions'
  | 'toDeleteFunctions'
> & {
  toAddFunction: FunctionForm
}

export type SetPersonneAdditionalWithCodeParams = Omit<
  SetPersonneAdditionalParams,
  'toAddFunctions'
  | 'toDeleteFunctions'
> & {
  additionalCode: FunctionForm
}
