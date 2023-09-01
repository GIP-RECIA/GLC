// @ts-ignore
import { capitalize } from '@/utils/stringUtils';
import { describe, expect, it } from 'vitest';

describe('stringUtils', () => {
  it('capitalize', () => {
    expect(capitalize('foo')).toBe('Foo');
  });
});
