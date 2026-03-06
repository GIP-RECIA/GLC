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

import type { AxiosResponse } from 'axios'
import type { StructureRestriction } from '@/types/index.ts'
import { instance as axios, errorHandler } from '@/utils/index.ts'

async function getRestrictions(
  id: number,
): Promise<StructureRestriction | undefined> {
  try {
    const response: AxiosResponse<StructureRestriction> = await axios.get(
      `/api/restriction/${id}`,
    )

    return response.data
  }
  catch (e) {
    errorHandler(e)
  }
}

async function saveRestrictions(
  id: number,
  member: string,
  group: boolean,
): Promise<boolean> {
  try {
    await axios.post(
      `/api/restriction/${id}`,
      {
        member,
        group,
      },
    )

    return true
  }
  catch (e) {
    errorHandler(e)

    return false
  }
}

export {
  getRestrictions,
  saveRestrictions,
}
