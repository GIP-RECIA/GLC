import { plugins } from '../../config';
// @ts-ignore
import AdministrativeView from '@/views/AdministrativeView.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('AdministrativeView', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(AdministrativeView, {
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
