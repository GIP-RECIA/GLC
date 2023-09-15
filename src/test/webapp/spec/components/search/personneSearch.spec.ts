import { plugins } from '../../config';
// @ts-ignore
import PersonneSearch from '@/components/search/PersonneSearch.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('PersonneSearch', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(PersonneSearch, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {
    //     searchList: [
    //       {
    //         id: 1,
    //         etat: 'Valide',
    //         categorie: 'Enseignant',
    //         source: 'AC-DEMO',
    //         cn: 'foo bar',
    //       },
    //     ],
    //   },
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
