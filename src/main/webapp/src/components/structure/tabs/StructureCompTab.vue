<script setup lang="ts">
import type { Etablissement } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import Disclosure from '@/components/Disclosure.vue'
import { Etat, etatMap } from '@/types/enums/index.ts'
import DisciplineCard from './comp/DisciplineCard.vue'
import FiliereWithDisciplines from './comp/FiliereWithDisciplines.vue'

defineProps<{
  structure?: Etablissement
}>()

const { t } = useI18n()

/* Filters */

enum Staff {
  Teaching = 'teaching',
  School = 'school',
  Collectivity = 'collectivity',
  Academic = 'academic',
}

const filters = [
  {
    id: 'staff',
    name: 'Personnel',
    type: 'checkbox',
    items: [
      {
        key: 'staff-all',
        value: 'Tous les personnels',
      },
      ...Object.values(Staff).map(staff => ({
        key: staff,
        value: t(`tab.${staff}`),
      })),
    ],
  },
  {
    id: 'state',
    name: 'État',
    type: 'checkbox',
    items: [
      {
        key: 'state-all',
        value: 'Tous les états',
      },
      ...Object.values(Etat).map(etat => ({
        key: etat,
        value: t(etatMap[etat].i18n),
      })),
    ],
  },
]
</script>

<template>
  <div>
    <r-filters
      :data="filters"
    />

    <Disclosure
      v-for="filiere in structure?.filieres"
      :key="`structure-filiere-${filiere.id}`"
      class="filiere"
    >
      <template #heading>
        <h2>
          {{ filiere.libelleFiliere }}
        </h2>
        <span class="count">
          {{ filiere.disciplines.length + (filiere.personnesWithoutDiscipline.length > 0 ? 1 : 0) }}
        </span>
      </template>

      <FiliereWithDisciplines
        :filiere="filiere"
      />
    </Disclosure>

    <Disclosure
      v-if="structure?.withoutFunctions"
      id="withoutFunctions"
    >
      <template #heading>
        <h2>
          SANS Fonctions
        </h2>
        <span class="count">
          {{ structure.withoutFunctions.length }}
        </span>
      </template>

      <DisciplineCard
        label="SANS Fonctions"
        :users="structure.withoutFunctions"
      />
    </Disclosure>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>

<style lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.disclosure {
  > header > button {
    > h2 {
      margin-bottom: 0;
    }

    > .count {
      opacity: 0.6;
    }
  }

  > .menu {
    margin-top: 4px;
  }

  &:has(> header > button:focus-visible) {
    outline-offset: 8px;
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    &.filiere > .menu > ul > li {
      &:only-child,
      &:has(> .r-card > .body > ul > li:nth-child(10)) {
        > .r-card > .body > ul {
          grid-template-columns: repeat(auto-fill, minmax(216px, 1fr));
        }
      }
    }

    &#withoutFunctions > .menu > .r-card > .body > ul {
      grid-template-columns: repeat(auto-fill, minmax(216px, 1fr));
    }
  }

  @media (width >= map.get($grid-breakpoints, xxl)) {
    &.filiere > .menu > ul > li {
      &:only-child,
      &:has(> .r-card > .body > ul > li:nth-child(10)) {
        > .r-card > .body > ul {
          grid-template-columns: repeat(auto-fill, minmax(264px, 1fr));
        }
      }
    }

    &#withoutFunctions > .menu > .r-card > .body > ul {
      grid-template-columns: repeat(auto-fill, minmax(264px, 1fr));
    }
  }
}
</style>
