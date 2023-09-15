import { plugins } from '../../config';
// @ts-ignore
import PersonneChip from '@/components/search/PersonneChip.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('PersonneChip', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(PersonneChip, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {
    //     personne: {
    //       id: 1,
    //       etat: 'Valide',
    //       categorie: 'Enseignant',
    //       source: 'AC-DEMO',
    //       cn: 'foo bar',
    //     },
    //   },
    // });

    await flushPromises();

    // TODO
    expect(true).toBe(true);
  });
});
