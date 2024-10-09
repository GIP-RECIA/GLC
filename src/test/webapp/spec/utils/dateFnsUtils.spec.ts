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
// @ts-expect-error project location
import { isAfterOrEqual, isBeforeOrEqual, isBetween } from '@/utils'
import { describe, expect, it } from 'vitest'

describe('dateFnsUtils', () => {
  const date = '2024-08-01'

  it('test 1 - isBeforeOrEqual', () => {
    expect(isBeforeOrEqual(date, date)).toBe(true)
    expect(isBeforeOrEqual(date, '2024-09-01')).toBe(true)
    expect(isBeforeOrEqual(date, '2024-07-30')).toBe(false)
  })

  it('test 1 - isAfterOrEqual', () => {
    expect(isAfterOrEqual(date, date)).toBe(true)
    expect(isAfterOrEqual(date, '2024-07-30')).toBe(true)
    expect(isAfterOrEqual(date, '2024-08-02')).toBe(false)
  })

  it('test 1 - isBetween', () => {
    expect(isBetween(date, '2024-07-30', date)).toBe(true)
    expect(isBetween(date, date, '2024-08-02')).toBe(true)
    expect(isBetween(date, '2024-07-01', '2024-07-30')).toBe(false)
    expect(isBetween(date, '2024-07-30', date, false)).toBe(false)
    expect(isBetween(date, date, '2024-08-02', false)).toBe(false)
    expect(isBetween(date, '2024-07-30', '2024-08-02', false)).toBe(true)
  })
})
