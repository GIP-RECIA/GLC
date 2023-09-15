import { plugins } from '../../config';
// @ts-ignore
import BaseModal from '@/components/modals/BaseModal.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('BaseModal', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(BaseModal, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {
    //     modelValue: true,
    //     title: '',
    //     showXmark: true,
    //   },
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
