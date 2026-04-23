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
import type { RowSelectionState, SortingState } from '@tanstack/vue-table'
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
import { h, ref, watch } from 'vue'
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

const filters = [
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
  {
    id: 'profil',
    name: 'Profil',
    type: 'checkbox',
    items: [
      {
        key: 'profil-all',
        value: 'Tous les profils',
      },
      ...Object.values(CategoriePersonne).map(cat => ({
        key: cat,
        value: t(categoriePersonneMap[cat].i18n),
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
      ...etatFilters.map(etat => ({
        key: etat,
        value: t(etatMap[etat].i18n),
      })),
    ],
  },
]

/* Actions */

function onUnlock(): void {

}

function onExport(): void {

}

/* Table */

const data = ref<AccountUser[]>([])

watch(
  () => props.structure?.personnes,
  (val) => {
    data.value = val ?? []
  },
  { immediate: true },
)

const columnHelper = createColumnHelper<AccountUser>()
const globalFilter = ref<string>()
const columns = [
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
    header: t('page.user.status.header'),
    cell: (info) => {
      const etat = {
        icon: getIconDefinition(info.row.original.local),
        ...etatMap[info.getValue()],
      }
      const suppressDate = info.row.original.dateSuppression
        ? format(info.row.original.dateSuppression, 'P')
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
          h(
            FontAwesomeIcon,
            {
              icon: etat.icon,
              size: 'lg',
              style: {
                color: etat.color,
              },
            },
          ),
        ],
      )
    },
    enableGlobalFilter: false,
  }),
  columnHelper.accessor('nom', {
    header: t('page.user.info.identity.lastName'),
  }),
  columnHelper.accessor('prenom', {
    header: t('page.user.info.identity.firstName'),
  }),
  columnHelper.accessor('uid', {
    header: t('page.user.info.account.uid'),
  }),
  columnHelper.accessor('categoriePersonne', {
    header: t('page.user.category.header'),
    cell: info => t(categoriePersonneMap[info.getValue()].i18n),
    enableGlobalFilter: false,
  }),
  columnHelper.accessor('login', {
    header: t('page.user.info.account.login'),
    cell: info => info.row.original.guichet
      ? t('externalLogin')
      : info.getValue(),
  }),
  columnHelper.accessor('email', {
    header: t('page.user.info.account.email'),
  }),
  columnHelper.accessor('dateModification', {
    header: t('page.user.info.context.sourceModificationDate'),
    enableGlobalFilter: false,
  }),
  {
    id: 'select',
    header: 'Actions',
    cell: ({ row }: { row: any }) => h(
      RouterLink,
      {
        to: { name: 'user', params: { userId: row.original.id } },
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
    enableGlobalFilter: false,
  },
]
const rowSelection = ref<RowSelectionState>({})
const sorting = ref<SortingState>([])

const table = useVueTable({
  get data() {
    return data.value
  },
  columns,
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
  },
  getCoreRowModel: getCoreRowModel(),
  getFilteredRowModel: getFilteredRowModel(),
  getPaginationRowModel: getPaginationRowModel(),
  getSortedRowModel: getSortedRowModel(),
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
})
</script>

<template>
  <div>
    <r-filters
      :data="filters"
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
        <tr
          v-for="row in table.getRowModel().rows"
          :key="row.id"
        >
          <td
            v-for="cell in row.getVisibleCells()"
            :key="cell.id"
          >
            <FlexRender
              :render="cell.column.columnDef.cell"
              :props="cell.getContext()"
            />
          </td>
        </tr>
      </tbody>
    </table>

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
</style>
