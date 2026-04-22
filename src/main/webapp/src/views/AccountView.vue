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
import type { FilterFn } from '@tanstack/vue-table'
import type { SearchStructure } from '@/types/index.ts'
import {
  faAngleLeft,
  faAngleRight,
  faAnglesLeft,
  faAnglesRight,
} from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import {

  getCoreRowModel,
  getFilteredRowModel,
  getPaginationRowModel,
  useVueTable,
} from '@tanstack/vue-table'
import { ref, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import { useStructuresQuery } from '@/services/queries/index.ts'
import { concatenate, normalize } from '@/utils/index.ts'

const { t } = useI18n()

const userSearch = ref<string>()

/* Table */

const { data: structures } = useStructuresQuery()

const data = ref<SearchStructure[]>([])

watch(
  structures,
  (val) => {
    data.value = val ?? []
  },
  { immediate: true },
)

const globalFilter = ref<string>()
const columns = [
  {
    accessorFn: (row: SearchStructure) => (
      concatenate(
        [
          row.nom,
          row.type,
          row.uai,
          row.siren,
          row.ville,
        ],
        ' ',
      )
    ),
    id: 'search',
  },
  {
    id: 'card',
    cell: ({ row }: { row: any }) => row.original,
    enableGlobalFilter: false,
  },
]

const fuzzyFilter: FilterFn<any> = (row, columnId, value) => {
  const rowValue = row.getValue<string>(columnId)

  return normalize(rowValue).includes(normalize(value))
}

const table = useVueTable({
  get data() {
    return data.value
  },
  columns,
  state: {
    get globalFilter() {
      return globalFilter.value
    },
  },
  onGlobalFilterChange: (val) => {
    globalFilter.value = val as string
  },
  getCoreRowModel: getCoreRowModel(),
  getFilteredRowModel: getFilteredRowModel(),
  getPaginationRowModel: getPaginationRowModel(),
  initialState: {
    pagination: {
      pageSize: 20,
    },
  },
  globalFilterFn: fuzzyFilter,
})
</script>

<template>
  <div class="container">
    <h1>
      {{ t('page.account.h1') }}
    </h1>

    <div class="users">
      <h2>
        {{ t('page.account.user.header') }}
      </h2>

      <div class="field">
        <div class="field-layout">
          <div class="field-container">
            <div class="middle">
              <label
                for="user-search"
              >
                {{ t('page.account.user.search') }}
              </label>
              <input
                id="user-search"
                v-model.trim="userSearch"
                type="text"
                placeholder=""
              >
            </div>
          </div>
          <div class="active-indicator" />
        </div>
      </div>
    </div>

    <div class="structures">
      <h2>
        {{ t('page.account.structure.header') }}
      </h2>

      <div class="field">
        <div class="field-layout">
          <div class="field-container">
            <div class="middle">
              <label
                for="structure-search"
              >
                {{ t('page.account.structure.search') }}
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

      <ul class="info-container">
        <li
          v-for="row in table.getRowModel().rows"
          :key="row.id"
        >
          <div class="structure-card">
            <RouterLink
              :to="{
                name: 'structure',
                params: { structureId: row.original.id },
              }"
            >
              {{ row.original.nom }}
              <span aria-hidden="true" />
            </RouterLink>

            <p
              v-if="row.original.type || row.original.uai"
              class="description"
            >
              {{ row.original.type }}
              <span v-if="row.original.uai">
                {{ row.original.uai }}
              </span>
            </p>
          </div>
        </li>
      </ul>

      <div
        v-show="table.getPageCount() > 1"
        class="pagination"
      >
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

        <p>
          <span class="sr-only">page</span>
          {{ table.getState().pagination.pageIndex + 1 }}
          <span class="sr-only">sur</span>
          <span aria-hidden="true">/</span>
          {{ table.getPageCount() }}
        </p>

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
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.container {
  margin-top: 32px;
  margin-bottom: 40px;
  display: flex;
  flex-direction: column;
  gap: 32px;

  > h1 {
    margin-bottom: 0;
  }

  > .users,
  > .structures {
    display: flex;
    flex-direction: column;
    gap: 16px;

    > h2 {
      margin-bottom: 0;
    }
  }

  .structures {
    > ul {
      @include unstyled-list;
      grid-auto-rows: 1fr;
      align-items: unset;
    }

    > .pagination {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
      padding: 20px;

      > p {
        width: 6em;
        text-align: center;
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    margin-bottom: 60px;
    gap: 48px;
  }
}

.info-container {
  display: grid;
  gap: 16px;

  @media (width >= map.get($grid-breakpoints, md)) {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    align-items: start;
  }
}

.structure-card {
  height: 100%;
  padding: 16px;
  position: relative;
  border-radius: 10px;
  box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
  outline-color: transparent;
  outline-offset: -1px;
  transition:
    outline 0.15s ease-out,
    box-shadow 0.15s ease-out;

  &:has(> a) {
    &:hover,
    &:has(:focus-visible) {
      outline: 2px solid var(--#{$prefix}primary);
      box-shadow: var(--#{$prefix}shadow-hover) HEXToRGBA(var(--#{$prefix}primary), 0.2);
    }
  }

  > a {
    @include unstyled-link;

    &:focus-visible {
      outline: none;
    }

    > span {
      position: absolute;
      z-index: 1;
      inset: 0;
    }
  }

  > .description {
    opacity: 0.6;
  }
}
</style>
