import { plugins } from '../../config';
// @ts-ignore
import PersonneModal from '@/components/modals/PersonneModal.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('PersonneModal', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(PersonneModal, {
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
