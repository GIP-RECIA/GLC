import { plugins } from '../../config';
// @ts-ignore
import AdditionalModal from '@/components/modals/AdditionalModal.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('AdditionalModal', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(AdditionalModal, {
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
