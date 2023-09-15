import { plugins } from '../../config';
// @ts-ignore
import CheckboxLayout from '@/components/layouts/CheckboxLayout.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('CheckboxLayout', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(CheckboxLayout, {
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
    //     selected: [],
    //     disabled: [],
    //   },
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
