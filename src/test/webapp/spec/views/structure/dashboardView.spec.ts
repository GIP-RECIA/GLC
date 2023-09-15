import { plugins } from '../../config';
// @ts-ignore
import DashboardView from '@/views/structure/DashboardView.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('DashboardView', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(DashboardView, {
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
