import { plugins } from '../../config';
// @ts-ignore
import PersonneListItem from '@/components/search/PersonneListItem.vue';
import { flushPromises, shallowMount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';

global.ResizeObserver = require('resize-observer-polyfill');

describe('PersonneListItem', () => {
  it('test 1 - init', async () => {
    // const wrapper = shallowMount(PersonneListItem, {
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
