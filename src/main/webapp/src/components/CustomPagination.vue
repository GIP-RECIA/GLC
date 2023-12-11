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
  const pages = Math.round(props.items ? props.items.length / props.itemsPerPage : 1);
  return pages > 0 ? pages : 1;
});

const showPage = (page: number) => {
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
  () => {
    showPage(1);
  },
  { immediate: true },
);

watch(
  () => props.itemsPerPage,
  () => {
    showPage(1);
  },
);
</script>

<template>
  <v-pagination v-if="nbPages > 1 || !hideSinglePage" v-model="currentPage" :length="nbPages" rounded="circle" />
</template>
