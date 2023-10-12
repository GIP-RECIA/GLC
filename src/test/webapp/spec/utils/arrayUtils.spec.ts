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
