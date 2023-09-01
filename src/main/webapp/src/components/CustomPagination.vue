<script setup lang="ts">
import { ref, watch } from 'vue';

const props = defineProps<{
  items: Array<any> | undefined;
  itemsPerPage: number;
  hideSinglePage?: boolean;
}>();

const emit = defineEmits<(event: 'update:page', payload: Array<any>) => void>();

const pagination = ref({
  page: 1,
  pages: 1,
});

watch(
  () => props.items,
  () => {
    init();
  },
);

watch(
  () => props.itemsPerPage,
  () => {
    init();
  },
);

const init = () => {
  pagination.value.page = 1;
  const pages = Math.round(props.items ? props.items.length / props.itemsPerPage : 1);
  pagination.value.pages = pages > 0 ? pages : 1;
  showPage(1);
};

const showPage = (page: number) => {
  if (typeof props.items !== 'undefined' && props.items !== null) {
    const items = props.items.filter((_, index) => {
      return page == 1
        ? index < props.itemsPerPage
        : index >= (page - 1) * props.itemsPerPage && index < page * props.itemsPerPage;
    });
    emit('update:page', items);
  }
};

init();
</script>

<template>
  <v-pagination
    v-if="pagination.pages > 1 || !hideSinglePage"
    v-model="pagination.page"
    :length="pagination.pages"
    rounded="circle"
    @update:model-value="showPage"
  />
</template>
