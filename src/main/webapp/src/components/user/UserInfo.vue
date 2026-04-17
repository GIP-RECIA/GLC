<script setup lang="ts">
import type { User } from '@/types/index.ts'
import { faCircle, faInfoCircle } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import SafeEmptyData from '@/components/SafeEmptyData.vue'
import { usePersonne } from '@/composables/index.ts'
import { categoriePersonneMap } from '@/types/enums/index.ts'

defineProps<{
  user?: User
}>()

const { t } = useI18n()

const {
  etat,
  suppressDate,
} = usePersonne()
</script>

<template>
  <div class="user-info">
    <div class="user-avatar">
      <img
        :src="user?.photo ?? '/images/icones/noPictureUser.svg'"
        alt="Photo de profile"
      >
    </div>

    <ul>
      <li>
        <h1>
          <SafeEmptyData
            :value="user?.cn"
          />
        </h1>
      </li>
      <li>
        <SafeEmptyData
          :value="
            user?.categorie
              ? t(categoriePersonneMap[user.categorie].i18n)
              : undefined
          "
        />
      </li>
      <li class="user-status">
        <span class="sr-only">
          {{ t('page.user.status.header') }}
        </span>
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
        <span
          v-if="suppressDate"
          :title="t('page.user.status.deletingDate', { suppressDate })"
        >
          <FontAwesomeIcon
            :icon="faInfoCircle"
            size="lg"
          />
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

.user-info {
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
      > h1 {
        margin-bottom: 0;
        line-height: 1em;
      }

      &.user-status {
        display: inline-flex;
        align-items: center;
        gap: 4px;
      }
    }
  }
}
</style>
