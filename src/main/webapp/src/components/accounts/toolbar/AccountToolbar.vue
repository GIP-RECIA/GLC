<script setup lang="ts">
import type { RouteLocationAsRelativeGeneric } from 'vue-router'
import { faArrowLeft, faHome } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { useSessionStorage } from '@vueuse/core'
import { computed, onMounted, onUnmounted, useTemplateRef } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import TabItem from './TabItem.vue'
import TabMenu from './TabMenu.vue'

interface TabItemT {
  id: string
  name: string
  to: RouteLocationAsRelativeGeneric
}

const route = useRoute()
const router = useRouter()

const items = useSessionStorage<TabItemT[]>(
  `${__APP_SLUG__}.account.tabs`,
  [],
)

const isItems = computed<boolean>(() => items.value.length > 0)

router.afterEach((to, from) => {
  const fromIndex = items.value.findIndex(i =>
    from.fullPath === router.resolve(i.to).fullPath,
  )

  const { structureId, userId } = to.params

  if (structureId && typeof structureId === 'string') {
    addItem({
      id: `structure-${structureId}`,
      name: `structure-${structureId}`,
      to: {
        name: 'structure',
        params: { structureId },
      },
    }, fromIndex)
  }

  if (userId && typeof userId === 'string') {
    addItem({
      id: `user-${userId}`,
      name: `user-${userId}`,
      to: {
        name: 'user',
        params: { userId },
      },
    }, fromIndex)
  }
})

function addItem(item: TabItemT, index: number = -1): void {
  const exists = items.value.some(i => i.id === item.id)
  if (exists)
    return

  if (index === -1) {
    items.value.push(item)
    return
  }

  items.value.splice(index + 1, 0, item)
}

function removeItem(id: string): void {
  const index = items.value.findIndex(i => i.id === id)

  if (index === -1)
    return

  const itemToRemove = items.value[index]

  const isActive
    = route.fullPath === router.resolve(itemToRemove.to).fullPath

  items.value.splice(index, 1)

  if (!isActive)
    return

  if (items.value.length === 0) {
    router.push({ name: 'account' })

    return
  }

  const nextIndex
    = index < items.value.length
      ? index
      : items.value.length - 1

  const nextItem = items.value[nextIndex]

  router.push(nextItem.to)
}

/* Gestion du scroll */

const tabList = useTemplateRef('tab-list')

function srollTabList(event: WheelEvent): void {
  if (!tabList.value || event.deltaX !== 0)
    return

  event.preventDefault()
  tabList.value.scrollBy({
    left: event.deltaY,
  })
}

onMounted(() => {
  tabList.value?.addEventListener('wheel', srollTabList)
})

onUnmounted(() => {
  tabList.value?.removeEventListener('wheel', srollTabList)
})
</script>

<template>
  <nav>
    <ul>
      <li>
        <router-link
          :to="{ name: 'index' }"
          class="btn-tertiary circle"
          title="Retour à l'accueil de l'application"
        >
          <FontAwesomeIcon
            :icon="faArrowLeft"
          />
        </router-link>
      </li>
      <li id="structures">
        <router-link
          :to="{ name: 'account' }"
          class="btn-tertiary circle"
          title="Retourner à la liste des structures"
        >
          <FontAwesomeIcon
            :icon="faHome"
          />
        </router-link>
      </li>
      <li id="tab-menu">
        <TabMenu
          :items="items"
          @remove-item="removeItem"
        />
      </li>
      <li
        v-show="isItems"
        id="tab-list"
      >
        <div>
          <ul ref="tab-list">
            <li
              v-for="item in items"
              :key="item.id"
            >
              <TabItem
                :id="item.id"
                :name="item.name"
                :to="item.to"
                @close="removeItem"
              />
            </li>
          </ul>
        </div>
      </li>
    </ul>
  </nav>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

nav {
  display: flex;
  align-items: center;
  padding: 8px;
  height: 50px;

  > ul {
    @include unstyled-list;
    display: flex;
    align-items: center;
    gap: 8px;
    min-width: 0;

    > li {
      flex: 0 0 auto;

      &:nth-child(-n + 2) {
        z-index: 3;
      }

      &#structures > a.router-link-exact-active {
        color: var(--#{$prefix}primary);
      }

      &#tab-list {
        $spacingY: 8px;
        $spacingX: 16px;
        display: none;
        flex: 0 1 auto;
        min-width: 0;
        margin-left: -$spacingX;
        margin-right: -$spacingX;

        > div {
          position: relative;

          &::before,
          &::after {
            content: '';
            position: absolute;
            top: 0;
            bottom: 0;
            width: 2 * $spacingX;
            z-index: 2;
          }

          &::before {
            left: -$spacingX;
            background: linear-gradient(90deg, var(--#{$prefix}body-bg) 60%, rgba(255, 255, 255, 0) 100%);
          }

          &::after {
            right: -$spacingX;
            background: linear-gradient(270deg, var(--#{$prefix}body-bg) 60%, rgba(255, 255, 255, 0) 100%);
          }

          > ul {
            @include unstyled-list;
            display: flex;
            align-items: center;
            gap: 8px;
            overflow-x: auto;
            scrollbar-width: none;
            scroll-behavior: smooth;
            white-space: nowrap;
            padding: $spacingY $spacingX;
            margin: -$spacingY 0;

            &:focus-visible {
              outline: 2px dotted var(--#{$prefix}primary);
              outline-offset: -4px;
              border-radius: 10px;
            }
          }
        }
      }
    }
  }

  @media (width >= map.get($grid-breakpoints, sm)) {
    > ul > li {
      &#tab-menu {
        display: none;
      }

      &#tab-list {
        display: unset;
      }
    }
  }
}
</style>
