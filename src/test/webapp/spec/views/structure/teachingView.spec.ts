import { plugins } from '../../config';
// @ts-ignore
import TeachingView from '@/views/structure/TeachingView.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('TeachingView', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(TeachingView, {
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
