// @ts-ignore
import { capitalize, concatenate } from '@/utils/stringUtils';
import { describe, expect, it } from 'vitest';

describe('stringUtils', () => {
  it('test 1 - capitalize', () => {
    expect(capitalize('foo')).toBe('Foo');
  });

  it('test 2 - concatenate', () => {
    expect(concatenate(['foo', 'bar'])).toBe('foobar');
    expect(concatenate(['foo', 'bar'], ', ')).toBe('foo, bar');
    expect(concatenate(['foo', 'bar'], ' - ')).toBe('foo - bar');
  });
});
