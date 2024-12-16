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

import { ResizeObserver } from '@juggle/resize-observer'
// import { plugins } from '../../config'
// // @ts-expect-error project location
// import CustomTabItem from '@/components/tab/CustomTabItem.vue'
import { flushPromises } from '@vue/test-utils'
import { describe, expect, it } from 'vitest'

globalThis.ResizeObserver = ResizeObserver

describe('customTabItem', () => {
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

    await flushPromises()

    // TODO
    expect(true).toBe(true)
  })
})
