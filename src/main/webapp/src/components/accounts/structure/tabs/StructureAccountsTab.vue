<script setup lang="ts">
import type { RowSelectionState, SortingState } from '@tanstack/vue-table'
import type { AccountUser, Structure } from '@/types/index.ts'
import {
  faAngleDown,
  faAngleLeft,
  faAngleRight,
  faAnglesLeft,
  faAnglesRight,
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
  getPaginationRowModel,
  getSortedRowModel,
  useVueTable,
} from '@tanstack/vue-table'
import { format } from 'date-fns'
import { h, ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import { CategoriePersonne, categoriePersonneMap, Etat, etatMap } from '@/types/enums/index.ts'
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
      ...Object.values(Etat).map(etat => ({
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

const INITIAL_PAGE_INDEX = 0
const columnHelper = createColumnHelper<AccountUser>()
const goToPageNumber = ref(INITIAL_PAGE_INDEX + 1)
const pageSizes = [20, 40]
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
    get sorting() {
      return sorting.value
    },
  },
  getCoreRowModel: getCoreRowModel(),
  getPaginationRowModel: getPaginationRowModel(),
  getSortedRowModel: getSortedRowModel(),
  initialState: {
    pagination: {
      pageSize: pageSizes[0],
    },
  },
  enableRowSelection: true,
  onRowSelectionChange: (updateOrValue) => {
    rowSelection.value = typeof updateOrValue === 'function'
      ? updateOrValue(rowSelection.value)
      : updateOrValue
  },
  enableMultiSort: true,
  maxMultiSortColCount: 2,
  onSortingChange: (updaterOrValue) => {
    sorting.value = typeof updaterOrValue === 'function'
      ? updaterOrValue(sorting.value)
      : updaterOrValue
  },
})

function handleGoToPage(e: any): void {
  const page = e.target.value ? Number(e.target.value) - 1 : 0
  goToPageNumber.value = page + 1
  table.setPageIndex(page)
}

function handlePageSizeChange(e: any): void {
  table.setPageSize(Number(e.target.value))
}
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

    <div>
      <div>
        <button
          class="btn-secondary small circle"
          :disabled="!table.getCanPreviousPage()"
          @click="() => table.setPageIndex(0)"
        >
          <FontAwesomeIcon
            :icon="faAnglesLeft"
          />
        </button>
        <button
          class="btn-secondary small circle"
          :disabled="!table.getCanPreviousPage()"
          @click="() => table.previousPage()"
        >
          <FontAwesomeIcon
            :icon="faAngleLeft"
          />
        </button>
        <button
          class="btn-secondary small circle"
          :disabled="!table.getCanNextPage()"
          @click="() => table.nextPage()"
        >
          <FontAwesomeIcon
            :icon="faAngleRight"
          />
        </button>
        <button
          class="btn-secondary small circle"
          :disabled="!table.getCanNextPage()"
          @click="() => table.setPageIndex(table.getPageCount() - 1)"
        >
          <FontAwesomeIcon
            :icon="faAnglesRight"
          />
        </button>
        <span>
          <span>Page</span>
          <strong>
            {{ table.getState().pagination.pageIndex + 1 }} of
            {{ table.getPageCount() }}
          </strong>
        </span>
        <span>
          | Go to page:
          <input
            type="number"
            :value="goToPageNumber"
            @change="handleGoToPage"
          >
        </span>
        <select
          :value="table.getState().pagination.pageSize"
          @change="handlePageSizeChange"
        >
          <option
            v-for="pageSize in pageSizes"
            :key="pageSize"
            :value="pageSize"
          >
            Show {{ pageSize }}
          </option>
        </select>
      </div>
    </div>
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
