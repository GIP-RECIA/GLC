<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import type {
  ExpandedState,
  Row,
  RowSelectionState,
  SortingState,
} from '@tanstack/vue-table'
import type { AccountUser, Structure } from '@/types/index.ts'
import {
  faAngleDown,
  faAngleUp,
  faEye,
  faFileExport,
  faLockOpen,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {
  createColumnHelper,
  FlexRender,
  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  getSortedRowModel,
  useVueTable,
} from '@tanstack/vue-table'
import { format } from 'date-fns'
import { computed, h, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import Pagination from '@/components/Pagination.vue'
import {
  CategoriePersonne,
  categoriePersonneMap,
  etatFilters,
  etatMap,
} from '@/types/enums/index.ts'
import { getIconDefinition, getStateLabel } from '@/utils/index.ts'
import IndeterminateCheckbox from './accounts/IndeterminateCheckbox.vue'
import '@gip-recia/ui-webcomponents/dist/r-filters.js'

const props = defineProps<{
  structure?: Structure
}>()

const { t } = useI18n()

/* Filters */

const categoriesPersonne = computed<CategoriePersonne[]>(() => (
  [...new Set(
    props.structure?.personnes
      .map(user => user.categoriePersonne),
  )]
))

const filters = computed(() => [
  {
    id: 'source',
    name: 'Source',
    type: 'checkbox',
    items: [
      {
        key: 'source-all',
        value: 'Toutes les sources',
      },
      {
        key: 'annu',
        value: 'Annuaire',
      },
      {
        key: 'local',
        value: 'Comple local',
      },
    ],
  },
  ...(categoriesPersonne.value.length > 1
    ? [{
        id: 'profil',
        name: 'Profil',
        type: 'checkbox',
        items: [
          {
            key: 'profil-all',
            value: 'Tous les profils',
          },
          ...Object.values(CategoriePersonne)
            .filter(cat => (
              categoriesPersonne.value.includes(cat)
            ))
            .map(cat => ({
              key: cat,
              value: t(categoriePersonneMap[cat].i18n),
            })),
        ],
      }]
    : []),
  {
    id: 'state',
    name: 'État',
    type: 'checkbox',
    items: [
      {
        key: 'state-all',
        value: 'Tous les états',
      },
      ...etatFilters.map(etat => ({
        key: etat,
        value: t(etatMap[etat].i18n),
      })),
    ],
  },
])

function updateFilters(e: CustomEvent): void {
  // eslint-disable-next-line unused-imports/no-unused-vars
  const { activeFilters } = e.detail
}

/* Actions */

function onUnlock(): void {

}

function onExport(): void {

}

/* Table */

const accounts = ref<AccountUser[]>([])

watch(
  () => props.structure?.personnes,
  (val) => {
    accounts.value = val ?? []
  },
  { immediate: true },
)

// const hasUid = computed<boolean>(() =>
//   accounts.value.some(row => row.uid != null),
// )

function renderEtat(row: Row<AccountUser>) {
  const etat = {
    icon: getIconDefinition(row.original.local),
    ...etatMap[row.original.etat],
  }
  const suppressDate = row.original.dateSuppression
    ? format(row.original.dateSuppression, 'P')
    : undefined
  const title = getStateLabel(
    etat.i18n,
    suppressDate,
    t,
  )

  return h(
    'span',
    {
      title,
    },
    [
      h(FontAwesomeIcon, {
        icon: etat.icon,
        size: 'lg',
        style: {
          color: etat.color,
        },
      }),
    ],
  )
}

function renderActions(row: Row<AccountUser>) {
  return [
    h(
      RouterLink,
      {
        to: {
          name: 'user',
          params: { userId: row.original.id },
        },
        class: 'btn-secondary small circle',
      },
      () => [
        h(
          'span',
          {
            title: 'Consulter',
          },
          [
            h(FontAwesomeIcon, {
              icon: faEye,
            }),
          ],
        ),
      ],
    ),
    h(
      'button',
      {
        type: 'button',
        ariaExpanded: row.getIsExpanded(),
        ariaControls: `user-menu-${row.original.id}`,
        class: 'btn-secondary small circle',
        onClick: row.getToggleExpandedHandler(),
      },
      [
        h(
          'span',
          {
            title: 'Développer',
          },
          [
            h(FontAwesomeIcon, {
              icon: faAngleDown,
              style: {
                rotate: row.getIsExpanded() ? '180deg' : undefined,
              },
            }),
          ],
        ),
      ],
    ),
  ]
}

const columnHelper = createColumnHelper<AccountUser>()
const globalFilter = ref<string>()
const columns = computed(() => [
  {
    id: 'select',
    header: ({ table }: { table: any }) => h(IndeterminateCheckbox, {
      checked: table.getIsAllPageRowsSelected(),
      indeterminate: table.getIsSomePageRowsSelected(),
      onChange: table.getToggleAllPageRowsSelectedHandler(),
    }),
    cell: ({ row }: { row: any }) => h(IndeterminateCheckbox, {
      checked: row.getIsSelected(),
      disabled: !row.getCanSelect(),
      onChange: row.getToggleSelectedHandler(),
    }),
    enableGlobalFilter: false,
  },
  columnHelper.accessor('etat', {
    id: 'etat',
    header: t('page.user.status.header'),
    cell: ({ row }) => renderEtat(row),
    enableGlobalFilter: false,
  }),
  columnHelper.accessor('nom', {
    id: 'nom',
    header: t('page.user.info.identity.lastName'),
  }),
  columnHelper.accessor('prenom', {
    id: 'prenom',
    header: t('page.user.info.identity.firstName'),
  }),
  // columnHelper.accessor('categoriePersonne', {
  //   header: t('page.user.category.header'),
  //   cell: info => t(categoriePersonneMap[info.getValue()].i18n),
  //   enableGlobalFilter: false,
  // }),
  // ...(
  //   hasUid.value
  //     ? [
  //         columnHelper.accessor('uid', {
  //           header: t('page.user.info.account.uid'),
  //         }),
  //       ]
  //     : []
  // ),
  // columnHelper.accessor('login', {
  //   header: t('page.user.info.account.login'),
  //   cell: info => info.row.original.guichet
  //     ? t('externalLogin')
  //     : info.getValue(),
  // }),
  // columnHelper.accessor('email', {
  //   header: t('page.user.info.account.email'),
  // }),
  // columnHelper.accessor('dateModification', {
  //   header: t('page.user.info.context.sourceModificationDate'),
  //   enableGlobalFilter: false,
  // }),
  columnHelper.display({
    id: 'actions',
    header: 'Actions',
    cell: ({ row }) => renderActions(row),
    enableGlobalFilter: false,
  }),
])
const rowSelection = ref<RowSelectionState>({})
const sorting = ref<SortingState>([])
const expanded = ref<ExpandedState>({})

const table = useVueTable({
  get data() {
    return accounts.value
  },
  get columns() {
    return columns.value
  },
  state: {
    get rowSelection() {
      return rowSelection.value
    },
    get globalFilter() {
      return globalFilter.value
    },
    get sorting() {
      return sorting.value
    },
    get expanded() {
      return expanded.value
    },
  },
  getCoreRowModel: getCoreRowModel(),
  getFilteredRowModel: getFilteredRowModel(),
  getPaginationRowModel: getPaginationRowModel(),
  getSortedRowModel: getSortedRowModel(),
  getRowCanExpand: () => true,
  initialState: {
    pagination: {
      pageSize: 20,
    },
  },
  enableRowSelection: true,
  onRowSelectionChange: (updateOrValue) => {
    rowSelection.value = typeof updateOrValue === 'function'
      ? updateOrValue(rowSelection.value)
      : updateOrValue
  },
  onGlobalFilterChange: (val) => {
    globalFilter.value = val as string
  },
  enableMultiSort: true,
  maxMultiSortColCount: 2,
  onSortingChange: (updaterOrValue) => {
    sorting.value = typeof updaterOrValue === 'function'
      ? updaterOrValue(sorting.value)
      : updaterOrValue
  },
  onExpandedChange: (updaterOrValue) => {
    expanded.value = typeof updaterOrValue === 'function'
      ? updaterOrValue(expanded.value)
      : updaterOrValue
  },
})
</script>

<template>
  <div>
    <r-filters
      v-dev
      :data="filters"
      @update-filters="updateFilters"
    />

    <div class="accounts-actions">
      <h2 class="sr-only">
        {{ t('page.structure.actions') }}
      </h2>

      <ul>
        <li>
          <button
            type="button"
            class="btn-primary small"
            @click="onUnlock"
          >
            {{ t('button.unlock') }}
            <FontAwesomeIcon
              :icon="faLockOpen"
            />
          </button>
        </li>
        <li>
          <button
            type="button"
            class="btn-primary small"
            @click="onExport"
          >
            {{ t('button.export') }}
            <FontAwesomeIcon
              :icon="faFileExport"
            />
          </button>
        </li>
      </ul>
    </div>

    <div class="field">
      <div class="field-layout">
        <div class="field-container">
          <div class="middle">
            <label
              for="structure-search"
            >
              {{ t('page.structure.accounts.search') }}
            </label>
            <input
              id="structure-search"
              v-model.trim="globalFilter"
              type="text"
              placeholder=""
            >
          </div>
        </div>
        <div class="active-indicator" />
      </div>
    </div>

    <div class="accounts-data">
      <table>
        <thead>
          <tr
            v-for="headerGroup in table.getHeaderGroups()"
            :key="headerGroup.id"
          >
            <th
              v-for="header in headerGroup.headers"
              :key="header.id"
              :colSpan="header.colSpan"
              :class="[
                header.column.getCanSort()
                  ? 'cursor-pointer select-none'
                  : '',
                header.column.columnDef.id,
              ]"
              @click="header.column.getToggleSortingHandler()?.($event)"
            >
              <FlexRender
                v-if="!header.isPlaceholder"
                :render="header.column.columnDef.header"
                :props="header.getContext()"
              />

              <FontAwesomeIcon
                v-if="header.column.getIsSorted()"
                :icon="
                  header.column.getIsSorted() === 'asc'
                    ? faAngleUp
                    : faAngleDown
                "
              />
            </th>
          </tr>
        </thead>
        <tbody>
          <template
            v-for="row in table.getRowModel().rows"
            :key="row.id"
          >
            <tr class="contentLine">
              <td
                v-for="cell in row.getVisibleCells()"
                :key="cell.id"
                :class="cell.column.columnDef.id"
              >
                <FlexRender
                  :render="cell.column.columnDef.cell"
                  :props="cell.getContext()"
                />
              </td>
            </tr>
            <tr
              v-show="row.getIsExpanded()"
              :id="`user-menu-${row.original.id}`"
              class="expandedLine"
            >
              <td :colspan="row.getAllCells().length">
                <div>
                  <div>{{ t(categoriePersonneMap[row.original.categoriePersonne].i18n) }}</div>
                  <div>{{ row.original.uid }}</div>
                  <div>{{ row.original.guichet ? t('page.user.office.external') : row.original.login }}</div>
                  <div>{{ row.original.email }}</div>
                  <div>{{ row.original.dateModification }}</div>
                </div>
              </td>
            </tr>
          </template>
        </tbody>
      </table>
    </div>

    <Pagination
      :table="table"
    />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.accounts-actions {
  flex: 0 1 auto;
  min-width: 320 - 2 * 16px;

  > ul {
    @include unstyled-list;
    display: flex;
    flex-wrap: wrap;
    justify-content: end;
    gap: 8px;
  }
}

.accounts-data {
  > table {
    display: grid;
    border-collapse: collapse;
    table-layout: fixed;
    width: 100%;

    > thead > tr > th,
    > tbody > tr > td {
      &.select,
      &.etat {
        padding: 12px;
        width: 40px;
        text-align: center;
      }
    }

    > thead {
      position: sticky;
      top: 0;
      background-color: var(--#{$prefix}body-bg);

      > tr > th {
        padding: 12px;
      }
    }

    > tbody {
      > tr {
        border-radius: 10px;
        transition: background-color 0.15s ease;

        &.contentLine {
          display: grid;
          grid-template-columns: 40px 1fr auto;
          grid-template-areas:
            'select nom    actions'
            'etat   prenom actions';
          align-items: center;
          white-space: nowrap;
          border-top: 1px solid var(--#{$prefix}stroke);

          > td {
            &:not(.select, .etat, .actions) {
              padding: 12px 16px;
            }

            &.select {
              grid-area: select;
              padding-bottom: 0;
            }

            &.etat {
              grid-area: etat;
              padding-top: 0;
            }

            &.nom {
              grid-area: nom;
              padding-bottom: 0;
            }

            &.prenom {
              grid-area: prenom;
              padding-top: 0;
            }

            &.actions {
              grid-area: actions;
              display: flex;
              flex-direction: column;
              align-items: center;
              justify-content: center;
              gap: 8px;
              padding: 8px;

              > :deep(button) > span > svg {
                transition: rotate 0.2s ease-in-out;
              }
            }
          }
        }

        &.expandedLine {
          > td > div {
            display: grid;
            grid-auto-flow: column;
            grid-auto-columns: 1fr;

            > div {
              padding: 12px 16px;
            }
          }
        }

        > td {
          vertical-align: middle;
        }

        &:hover,
        &:has(:focus-visible) {
          background-color: HEXToRGBA(var(--#{$prefix}btn-secondary-hover), 0.4);
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    > table {
      display: table;

      > thead > tr > th,
      > tbody > tr > td {
        &.actions {
          width: 100px;
        }
      }

      > thead > tr > th {
        padding: 12px 16px;
      }

      > tbody > tr.contentLine {
        display: table-row;

        > td {
          &.select {
            padding-bottom: 12px;
          }

          &.etat {
            padding-top: 12px;
          }

          &.nom {
            padding-bottom: 12px;
          }

          &.prenom {
            padding-top: 12px;
          }

          &.actions {
            flex-direction: row;
          }
        }
      }
    }
  }
}
</style>
