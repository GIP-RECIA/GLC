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
import { computed, ref, watch } from 'vue';

const props = defineProps<{
  items: Array<any> | undefined;
  itemsPerPage: number;
  hideSinglePage?: boolean;
}>();

const emit = defineEmits<(event: 'update:page', payload: Array<any>) => void>();

const page = ref<number>(1);
const currentPage = computed<number>({
  get() {
    return page.value;
  },
  set(newValue) {
    page.value = newValue;
    showPage(newValue);
  },
});

const nbPages = computed<number>(() => {
  const pages = Math.ceil(props.items ? props.items.length / props.itemsPerPage : 1);
  return pages > 0 ? pages : 1;
});

const showPage = (page: number): void => {
  if (typeof props.items !== 'undefined' && props.items !== null) {
    const items: Array<any> = props.items.filter((_, index) => {
      return page == 1
        ? index < props.itemsPerPage
        : index >= (page - 1) * props.itemsPerPage && index < page * props.itemsPerPage;
    });
    emit('update:page', items);
  }
};

watch(
  () => props.items,
  () => showPage(1),
  { immediate: true },
);

watch(
  () => props.itemsPerPage,
  () => showPage(1),
);
</script>

<template>
  <v-pagination v-if="nbPages > 1 || !hideSinglePage" v-model="currentPage" :length="nbPages" rounded="circle" />
</template>
