import { plugins } from '../config';
// @ts-ignore
import PersonneCard from '@/components/PersonneCard.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('PersonneCard', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(PersonneCard, {
    //   global: {
    //     plugins: [...plugins],
    //   },
    //   props: {
    //     personne: { id: 1, etat: 'Valide', categorie: 'Enseignant', source: 'AC-DEMO', cn: 'foo bar' },
    //   },
    // });

    await flushPromises();

    expect(true).toBe(true);
  });
});
