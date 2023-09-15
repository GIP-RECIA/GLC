import { plugins } from '../../config';
// @ts-ignore
import AccountFilter from '@/components/filter/AccountFilter.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('AccountFilter', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(AccountFilter, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {
    //     searchList: [],
    //   },
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
