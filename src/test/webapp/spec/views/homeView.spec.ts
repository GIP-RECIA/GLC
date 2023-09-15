import { plugins } from '../config';
// @ts-ignore
import HomeView from '@/views/HomeView.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('HomeView', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(HomeView, {
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
