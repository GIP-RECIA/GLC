<script setup lang="ts">
import type { User } from '@/types/index.ts'
import { useI18n } from 'vue-i18n'
import UserAccount from './information/UserAccount.vue'
import UserContext from './information/UserContext.vue'
import UserIdentity from './information/UserIdentity.vue'
import UserRelation from './information/UserRelation.vue'
import UserStructure from './information/UserStructure.vue'

defineProps<{
  user?: User
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

    <div
      v-if="user?.relations && user.relations.length > 0"
      class="relations"
    >
      <h2>
        {{ t('page.user.relation.header') }}
      </h2>
      <ul class="info-container">
        <li
          v-for="(relation, index) in user.relations"
          :key="`relation-${index}`"
        >
          <UserRelation
            :relation="relation"
          />
        </li>
      </ul>
    </div>

    <div class="structures">
      <h2>
        {{ t('page.user.structure.header') }}
      </h2>

      <ul class="info-container">
        <li
          v-for="structure in user?.listeStructures"
          :key="`user-structure-${structure.id}`"
        >
          <UserStructure
            :structure="structure"
          />
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

.relations,
.structures {
  > ul {
    @include unstyled-list;
    grid-auto-rows: 1fr;
    align-items: unset;
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
