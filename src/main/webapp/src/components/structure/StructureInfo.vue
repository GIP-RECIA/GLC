<script setup lang="ts">
import type { Structure } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'

defineProps<{
  structure?: Structure
}>()

const { t } = useI18n()
</script>

<template>
  <div class="structure-info">
    <div class="structure-logo">
      <img
        :src="structure?.logo ?? '/annuaire_images/default_banner_v1.jpg'"
        :alt="t('page.structure.logo')"
      >
    </div>

    <ul>
      <li>
        <h1>
          <SafeEmptyData
            :value="structure?.nom"
          />
        </h1>
      </li>
      <li v-if="structure">
        {{ structure.type }}
        <span v-show="structure.uai">
          {{ structure.uai }}
        </span>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.structure-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;

  > .structure-logo {
    flex: 0 0 auto;
    width: auto;
    height: 120px;
    border-radius: 10px;
    box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
    overflow: hidden;
    aspect-ratio: 9 / 4;

    > img {
      object-fit: cover;
      height: 100%;
      width: 100%;
      display: flex;
      align-items: center;
      justify-content: center;
    }
  }

  > ul {
    @include unstyled-list;
    display: flex;
    flex-direction: column;
    gap: 4px;

    > li {
      > h1 {
        margin-bottom: 0;
        line-height: 1em;
      }

      &:nth-child(2) {
        opacity: 0.6;
        font-size: var(--#{$prefix}font-size-sm);
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    flex-direction: row;
  }
}
</style>
