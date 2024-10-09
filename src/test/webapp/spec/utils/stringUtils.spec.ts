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
import { capitalize, concatenate } from '@/utils/stringUtils'
import { describe, expect, it } from 'vitest'

describe('stringUtils', () => {
  it('test 1 - capitalize', () => {
    expect(capitalize('foo')).toBe('Foo')
  })

  it('test 2 - concatenate', () => {
    expect(concatenate(['foo', 'bar'])).toBe('foobar')
    expect(concatenate(['foo', 'bar'], ', ')).toBe('foo, bar')
    expect(concatenate(['foo', 'bar'], ' - ')).toBe('foo - bar')
  })
})
