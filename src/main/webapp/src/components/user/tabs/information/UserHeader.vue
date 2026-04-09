<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
import { faCircle } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { usePersonne } from '@/composables/index.ts'
import { getCategoriePersonne } from '@/utils/index.ts'

defineProps<{
  user?: Personne
}>()

const { t } = useI18n()

const {
  etat,
} = usePersonne()
</script>

<template>
  <div class="header-card">
    <div class="user-avatar">
      <img
        :src="user?.photo ?? '/images/icones/noPictureUser.svg'"
        alt="Photo de profile"
      >
    </div>

    <ul>
      <li>
        <SafeEmptyData
          :value="user?.cn"
        />
      </li>
      <li>
        <SafeEmptyData
          :value="
            user?.categorie
              ? t(getCategoriePersonne(user.categorie).i18n)
              : undefined
          "
        />
      </li>
      <li>
        <div class="user-status">
          <FontAwesomeIcon
            :icon="faCircle"
            :style="{
              color: etat.color,
            }"
            size="lg"
          />
          <SafeEmptyData
            :value="t(etat.i18n)"
          />
        </div>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.header-card {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 16px;

  > .user-avatar {
    flex: 0 0 auto;
    width: 100px;
    height: 100px;
    border-radius: 50%;
    box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
    overflow: hidden;

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
      &:first-child {
        font-size: var(--#{$prefix}font-size-lg);
        font-weight: bold;
        line-height: 1em;
      }

      > .user-status {
        display: inline-flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}
</style>
