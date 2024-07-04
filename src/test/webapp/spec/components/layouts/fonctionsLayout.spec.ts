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
import { plugins } from '../../config';
// @ts-ignore
import FonctionsLayout from '@/components/layouts/FonctionsLayout.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('FonctionsLayout', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(FonctionsLayout, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {
    //     filieres: [
    //       {
    //         id: 1,
    //         codeFiliere: '',
    //         libelleFiliere: '',
    //         source: '',
    //         disciplines: [{ id: 1, code: '', disciplinePoste: '', source: '', personnes: [] }],
    //       },
    //     ],
    //     fonctions: [],
    //   },
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
