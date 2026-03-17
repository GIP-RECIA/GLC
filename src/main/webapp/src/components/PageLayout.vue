<script setup lang="ts">
import { faArrowLeft } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'

defineProps<{
  title: string
}>()
</script>

<template>
  <div class="page-layout">
    <header>
      <div class="heading">
        <slot name="back">
          <router-link
            :to="{ name: 'index' }"
            class="btn-tertiary circle"
            title="Retour à l'accueil de l'application"
          >
            <FontAwesomeIcon :icon="faArrowLeft" />
          </router-link>
        </slot>
        <h1>{{ title }}</h1>
      </div>
      <slot name="header" />
    </header>
    <slot />
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;

.page-layout {
  display: grid;
  gap: 32px;

  > header {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    gap: 24px;

    > .heading {
      align-self: flex-start;
      display: inline-flex;
      align-items: center;
      column-gap: 16px;

      > .btn-tertiary {
        align-self: flex-start;
        font-size: var(--#{$prefix}font-size-md);
      }

      > h1 {
        margin: 0;
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, md)) {
    gap: 48px;

    > header {
      flex-direction: row;

      > .heading {
        align-self: unset;
      }
    }
  }
}
</style>
