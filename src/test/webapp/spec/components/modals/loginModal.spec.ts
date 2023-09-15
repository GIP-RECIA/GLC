import { plugins } from '../../config';
// @ts-ignore
import LoginModal from '@/components/modals/LoginModal.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('LoginModal', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(LoginModal, {
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
