import { plugins } from '../../config';
// @ts-ignore
import AccountView from '@/views/AccountView.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('AccountView', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(AccountView, {
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
