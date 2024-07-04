/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
