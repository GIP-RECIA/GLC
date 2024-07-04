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
// @ts-ignore
import { isArrayOf } from '@/utils/arrayUtils';
import { describe, expect, it } from 'vitest';

describe('arrayUtils', () => {
  it('test 1 - isArrayOf', () => {
    expect(isArrayOf(['foo', 'bar'], 'string')).toBe(true);
    expect(isArrayOf([1, 2, 3, 4], 'number')).toBe(true);
    expect(isArrayOf([true, false], 'boolean')).toBe(true);
    expect(isArrayOf([{ name: 'foo' }, { name: 'bar' }], 'object')).toBe(true);
    expect(isArrayOf([{ name: 'foo' }, 1, 'bar'], 'object')).toBe(false);
    expect(isArrayOf([], 'object')).toBe(true);
  });
});
