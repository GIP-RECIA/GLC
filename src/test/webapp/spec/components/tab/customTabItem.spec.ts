import { plugins } from '../../config';
// @ts-ignore
import CustomTabItem from '@/components/tab/CustomTabItem.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('CustomTabItem', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(CustomTabItem, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {
    //     id: 1,
    //     title: '',
    //     link: { name: 'structure', params: { structureId: 1 } },
    //     selected: false,
    //   },
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
