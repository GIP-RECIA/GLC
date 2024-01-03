import { plugins } from '../../config';
// @ts-ignore
import ChipsFilter from '@/components/filter/ChipsFilter.vue';
import { flushPromises, mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('ChipsFilter', () => {
  it('test 1 - init', async () => {
    const wrapper = mount(ChipsFilter, {
      global: {
        plugins: [...plugins],
      },
      props: {
        items: [
          { id: 1, i18n: 'foo' },
          { id: 2, i18n: 'bar' },
        ],
      },
    });

    await flushPromises();

    expect(wrapper.text()).toContain('foo');
    expect(wrapper.text()).toContain('bar');
  });
});
