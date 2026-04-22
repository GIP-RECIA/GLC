import type { ShallowRef } from 'vue'
import { ref } from 'vue'

export function useTabs({
  tabs,
  tabsRefs,
}: {
  tabs: string[]
  tabsRefs: Readonly<ShallowRef<HTMLButtonElement[] | null>>
}) {
  const activeTab = ref<number>(0)

  function setActiveTab(tab: number, focus: boolean = false): void {
    activeTab.value = tab
    if (focus && tabsRefs.value)
      tabsRefs.value[tab]?.focus()
  }

  function changeActiveTab(e: KeyboardEvent): void {
    let index: number | undefined
    const active = activeTab.value

    switch (e.key) {
      case 'ArrowLeft':
        e.preventDefault()
        index = active - 1 > -1
          ? active - 1
          : tabs.length - 1
        break
      case 'ArrowRight':
        e.preventDefault()
        index = active + 1 < tabs.length
          ? active + 1
          : 0
        break
      case 'Home':
        e.preventDefault()
        index = 0
        break
      case 'End':
        e.preventDefault()
        index = tabs.length - 1
        break
      default:
        index = undefined
        break
    }

    if (
      index === undefined
      || active === index
    ) {
      return
    }

    setActiveTab(index, true)
  }

  return {
    activeTab,
    setActiveTab,
    changeActiveTab,
  }
}
