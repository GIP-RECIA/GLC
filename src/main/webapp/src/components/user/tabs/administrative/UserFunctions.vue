<!--
 Copyright (C) 2023 GIP-RECIA, Inc.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->

<script setup lang="ts">
import type { Discipline, Filiere, PersonneFonction } from '@/types/index.ts'
import { computed } from 'vue'
import { getDateFin } from '@/utils/index.ts'
import UserFiliere from './UserFiliere.vue'

const props = withDefaults(
  defineProps<{
    filieres: Filiere[] | undefined
    fonctions: PersonneFonction[] | undefined
    clickable?: boolean
  }>(),
  {
    clickable: false,
  },
)
const emit = defineEmits<{
  tagClick: [function: PersonneFonction]
}>()

const filteredFilieres = computed<Filiere[]>(() => {
  if (!props.filieres?.length || !props.fonctions?.length)
    return []

  const fonctionsMap = new Map<number, Map<number, PersonneFonction>>()

  for (const fun of props.fonctions) {
    let disciplineMap = fonctionsMap.get(fun.filiere.id)

    if (!disciplineMap) {
      disciplineMap = new Map()
      fonctionsMap.set(fun.filiere.id, disciplineMap)
    }

    if (!disciplineMap.has(fun.discipline.id))
      disciplineMap.set(fun.discipline.id, fun)
  }

  return props.filieres
    .filter(f => fonctionsMap.has(f.id))
    .map((filiere) => {
      const disciplineMap = fonctionsMap.get(filiere.id)!

      const disciplines = filiere.disciplines
        .filter(d => disciplineMap.has(d.id))
        .map((discipline) => {
          const fonction = disciplineMap.get(discipline.id)

          return {
            ...discipline,
            endInfo: fonction?.dateFin
              ? getDateFin(fonction.dateFin)
              : undefined,
          }
        })

      return { ...filiere, disciplines }
    })
})

function onTagClick(
  filiere: Filiere,
  discipline: Discipline,
): void {
  const fonction = props.fonctions?.find(f => (
    f.filiere.id === filiere.id
    && f.discipline.id === discipline.id
  ))

  if (!fonction)
    return

  emit('tagClick', {
    ...fonction,
    dateFin: discipline.endInfo?.date,
  })
}
</script>

<template>
  <UserFiliere
    v-for="filiere in filteredFilieres"
    :key="`filiere-card-${filiere.codeFiliere}`"
    :filiere="filiere"
    :clickable="clickable"
    @tag-click="onTagClick"
  />
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
