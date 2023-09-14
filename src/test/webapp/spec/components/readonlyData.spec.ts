// @ts-ignore
import ReadonlyData from '@/components/ReadonlyData.vue';
import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

describe('ReadonlyData', () => {
  it('test 1 - label only', async () => {
    const wrapper = mount(ReadonlyData, { props: { label: 'foo' } });

    expect(wrapper.text()).toContain('foo');
    expect(wrapper.text()).toContain('-');
  });

  it('test 2 - label with value', async () => {
    const wrapper = mount(ReadonlyData, { props: { label: 'foo', value: 'bar' } });

    expect(wrapper.text()).toContain('foo');
    expect(wrapper.text()).toContain('bar');
    expect(wrapper.text()).not.toContain('-');
  });
});
