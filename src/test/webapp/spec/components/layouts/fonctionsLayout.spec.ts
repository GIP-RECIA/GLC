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
