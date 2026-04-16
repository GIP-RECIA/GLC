<script setup lang="ts">
import type { SimplePersonne } from '@/types/index.ts'
import { faUser } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import { etatMap } from '@/types/enums/index.ts'

const props = defineProps<{
  user: SimplePersonne
}>()

const { t } = useI18n()

const etat = computed(() => ({
  icon: faUser,
  ...etatMap[props.user.etat],
}))
</script>

<template>
  <div class="user-link">
    <span
      :title="t(etat.i18n)"
    >
      <FontAwesomeIcon
        :icon="etat.icon"
        size="lg"
        :style="{
          color: etat.color,
        }"
      />
    </span>
    <RouterLink
      :to="{ name: 'user', params: { userId: user.id } }"
    >
      {{ user.cn }}
      <span aria-hidden="true" />
    </RouterLink>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.user-link {
  display: flex;
  gap: 8px;
  height: 100%;
  padding: 8px;
  position: relative;
  border-radius: 10px;
  outline-color: transparent;
  outline-offset: -1px;
  transition:
    outline 0.15s ease-out,
    box-shadow 0.15s ease-out;

  &:hover,
  &:has(:focus-visible) {
    outline: 2px solid var(--#{$prefix}primary);
  }

  > span {
    z-index: 2;
  }

  > a {
    @include unstyled-link;
    flex: 1 1 auto;
    white-space: nowrap;
    text-overflow: ellipsis;
    overflow: hidden;

    &:focus-visible {
      outline: none;
    }

    > span {
      position: absolute;
      z-index: 1;
      inset: 0;
    }
  }
}
</style>
