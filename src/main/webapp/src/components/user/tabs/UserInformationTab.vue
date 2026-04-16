<script setup lang="ts">
import type { Personne } from '@/types/index.ts'
import { faHouseUser, faLandmark } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useI18n } from 'vue-i18n'
import { RouterLink } from 'vue-router'
import UserAccount from './information/UserAccount.vue'
import UserContext from './information/UserContext.vue'
import UserIdentity from './information/UserIdentity.vue'

defineProps<{
  user?: Personne
}>()

const { t } = useI18n()
</script>

<template>
  <div>
    <div class="general-information">
      <h2>
        {{ t('page.user.info.header') }}
      </h2>

      <div class="info-container">
        <UserIdentity
          :user="user"
        />

        <UserAccount
          :user="user"
        />

        <UserContext
          :user="user"
        />
      </div>
    </div>

    <div class="structure-information">
      <h2>
        {{ t('page.user.structure.header') }}
      </h2>

      <ul class="info-container">
        <li
          v-for="structure in user?.listeStructures"
          :key="`user-structure-${structure.id}`"
        >
          <div class="structure-card">
            <div class="info">
              <RouterLink
                :to="{
                  name: 'structure',
                  params: { structureId: structure.id },
                }"
              >
                {{ structure.nom }}
                <span aria-hidden="true" />
              </RouterLink>

              <p v-if="structure.type || structure.uai">
                {{ structure.type }}
                <span v-if="structure.uai">
                  {{ structure.uai }}
                </span>
              </p>
            </div>

            <div
              v-if="
                structure.structureRattachement
                  || structure.structureCourante
              "
              class="icons"
            >
              <span
                v-show="structure.structureRattachement"
                :title="t('page.user.structure.admin')"
              >
                <FontAwesomeIcon
                  :icon="faLandmark"
                  size="lg"
                />
              </span>
              <span
                v-show="structure.structureCourante"
                :title="t('page.user.structure.current')"
                class="current"
              >
                <FontAwesomeIcon
                  :icon="faHouseUser"
                  size="lg"
                />
              </span>
            </div>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.structure-information {
  > ul {
    @include unstyled-list;
    grid-auto-rows: 1fr;
    align-items: unset;

    > li > .structure-card {
      display: flex;
      gap: 8px;
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

      &:hover,
      &:has(:focus-visible) {
        outline: 2px solid var(--#{$prefix}primary);
        box-shadow: var(--#{$prefix}shadow-hover) HEXToRGBA(var(--#{$prefix}primary), 0.2);
      }

      > .info {
        flex: 1 1 auto;

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

        > p {
          opacity: 0.6;
        }
      }

      > .icons {
        flex: 0 0 auto;
        display: grid;
        grid-template-rows: repeat(2, 1fr);
        align-items: center;
        gap: 4px;

        > * {
          z-index: 1;
        }

        > .current {
          grid-row: 2;
        }
      }
    }
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
</style>
