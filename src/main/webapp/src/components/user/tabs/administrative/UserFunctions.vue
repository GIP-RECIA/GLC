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
const emit = defineEmits({
  itemClic(payload: PersonneFonction | undefined): boolean {
    return !!payload
  },
})

const filteredFilieres = computed<Filiere[]>(() => {
  if (!props.filieres)
    return []
  const fonctions = props.fonctions
    ? props.fonctions
    : []
  const filiereIds = [
    ...new Set(fonctions.map(({ filiere }) => filiere)),
  ]

  return props.filieres
    .filter(({ id }) => filiereIds.includes(id))
    .map((filiere) => {
      const filiereFonctions = fonctions
        .filter(fonction => fonction.filiere === filiere.id)
      const disciplineIds = filiereFonctions
        .map(fonction => fonction.discipline)

      const disciplines = filiere.disciplines
        .filter(({ id }) => disciplineIds.includes(id))
        .map((discipline) => {
          const dateFin = filiereFonctions
            .find(fonction => fonction.discipline === discipline.id)
            ?.dateFin

          return {
            ...discipline,
            endInfo: dateFin
              ? getDateFin(dateFin)
              : undefined,
          }
        })

      return { ...filiere, disciplines }
    })
})

function onClick(
  filiere: Filiere,
  discipline: Discipline,
): void {
  let fonction = props.fonctions?.find(
    fonction => (
      fonction.filiere === filiere.id
      && fonction.discipline === discipline.id
    ),
  )
  if (fonction) {
    fonction = {
      ...fonction,
      dateFin: discipline.endInfo?.date,
    }
  }
  emit('itemClic', fonction)
}
</script>

<template>
  <UserFiliere
    v-for="filiere in filteredFilieres"
    :key="`filiere-card-${filiere.codeFiliere}`"
    :filiere="filiere"
    :clickable="clickable"
    @tag-click="onClick"
  />
</template>

<style scoped lang="scss">
@use 'sass:map';
@use '@gip-recia/ui/core/variables' as *;
@use '@gip-recia/ui/functions' as *;
@use '@gip-recia/ui/mixins' as *;
</style>
