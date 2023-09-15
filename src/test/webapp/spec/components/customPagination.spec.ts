import { plugins } from '../config';
// @ts-ignore
import CustomPagination from '@/components/CustomPagination.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('CustomPagination', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(CustomPagination, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {
    //     items: [],
    //     itemsPerPage: 20,
    //     hideSinglePage: true,
    //   },
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
