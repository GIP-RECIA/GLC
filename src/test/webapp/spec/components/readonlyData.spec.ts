// @ts-ignore
import ReadonlyData from '@/components/ReadonlyData.vue';
import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

describe('ReadonlyData', () => {
  it('label only', async () => {
    const wrapper = mount(ReadonlyData, { props: { label: 'foo' } });

    expect(wrapper.text()).toContain('foo');
    expect(wrapper.text()).toContain('-');
  });

  it('label with value', async () => {
    const wrapper = mount(ReadonlyData, { props: { label: 'foo', value: 'bar' } });

    expect(wrapper.text()).toContain('foo');
    expect(wrapper.text()).toContain('bar');
    expect(wrapper.text()).not.toContain('-');
  });
});
