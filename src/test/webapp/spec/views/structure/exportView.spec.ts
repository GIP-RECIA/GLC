import { plugins } from '../../config';
// @ts-ignore
import ExportView from '@/views/structure/ExportView.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('ExportView', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(ExportView, {
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
