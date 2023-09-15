import { plugins } from '../../config';
// @ts-ignore
import CustomTabBar from '@/components/tab/CustomTabBar.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('CustomTabBar', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(CustomTabBar, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {},
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
