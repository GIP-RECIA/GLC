<script setup lang="ts">
import type { Etablissement } from '@/types/index.ts'
import { faUser } from '@fortawesome/free-solid-svg-icons'
import { FontAwesomeIcon } from '@fortawesome/vue-fontawesome'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { Etat, etatMap } from '@/types/enums/index.ts'

const props = defineProps<{
  structure?: Etablissement
}>()

defineEmits<{
  selectedState: [Etat]
}>()

const { t } = useI18n()

const accountStates = computed(() => (
  [
    Etat.Invalide,
    Etat.Valide,
    Etat.Bloque,
    Etat.Delete,
    Etat.Deleting,
    Etat.Incertain,
  ].map(etat => ({
    etat,
    icon: faUser,
    count: props.structure?.personnes.filter(personne => personne.etat === etat).length ?? 0,
    ...etatMap[etat],
  }))
))
</script>

<template>
  <div class="accounts-states">
    <h2>
      {{ t('page.structure.dashboard.accountsStates') }}
    </h2>

    <ul>
      <li
        v-for="etat in accountStates"
        :key="`account-state-${etat.etat}`"
      >
        <button
          type="button"
          @click="$emit('selectedState', etat.etat)"
        >
          <div>
            <span class="count">
              {{ etat.count?.toLocaleString() }}
            </span>
            <span class="status">
              {{ t(etat.i18n) }}
            </span>
          </div>
          <FontAwesomeIcon
            :icon="etat.icon"
            size="3x"
            :style="{
              color: etat.color,
            }"
          />
        </button>
      </li>
    </ul>
  </div>
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;

.accounts-states {
  > ul {
    @include unstyled-list;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    grid-auto-rows: 1fr;
    gap: 16px;

    > li > button {
      display: flex;
      align-items: center;
      gap: 8px;
      border-radius: 10px;
      box-shadow: var(--#{$prefix}shadow-neutral) HEXToRGBA($black, 0.1);
      padding: 12px;
      width: 100%;
      height: 100%;

      > div {
        flex: 1 1 auto;
        display: flex;
        flex-direction: column;

        > .count {
          font-size: var(--#{$prefix}font-size-xxl);
        }

        > .status {
          font-size: var(--#{$prefix}font-size-sm);
          opacity: 0.6;
        }
      }

      > svg {
        flex: 0 0 auto;
      }
    }
  }
}
</style>
