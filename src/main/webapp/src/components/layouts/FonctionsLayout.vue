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
import type { Discipline, Filiere, PersonneFonction } from '@/types'
import { format } from 'date-fns'
import { storeToRefs } from 'pinia'
import { computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { useConfigurationStore } from '@/stores'
import { getDateFin } from '@/utils'

const props = withDefaults(
  defineProps<{
    filieres: Array<Filiere> | undefined
    fonctions: Array<PersonneFonction> | undefined
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
const configurationStore = useConfigurationStore()
const { currentStructureId } = storeToRefs(configurationStore)

const { t } = useI18n()

const etabFonctions = computed<Array<PersonneFonction>>(() => {
  return props.fonctions ? props.fonctions.filter(({ structure }) => structure === currentStructureId.value) : []
})

const filteredFilieres = computed<Array<Filiere>>(() => {
  if (!props.filieres)
    return []
  const filiereIds = [...new Set(etabFonctions.value.map(({ filiere }) => filiere))]

  return props.filieres
    .filter(({ id }) => filiereIds.includes(id))
    .map((filiere) => {
      const filiereFonctions = etabFonctions.value.filter(fonction => fonction.filiere === filiere.id)
      const disciplineIds = filiereFonctions.map(fonction => fonction.discipline)

      const disciplines = filiere.disciplines
        .filter(({ id }) => disciplineIds.includes(id))
        .map((discipline) => {
          const dateFin = filiereFonctions.find(fonction => fonction.discipline === discipline.id)?.dateFin

          return { ...discipline, endInfo: dateFin ? getDateFin(dateFin) : undefined }
        })

      return { ...filiere, disciplines }
    })
})

// eslint-disable-next-line unused-imports/no-unused-vars
function onClick(filiere: Filiere, discipline: Discipline): void {
  let fonction = etabFonctions.value.find(
    fonction => fonction.filiere === filiere.id && fonction.discipline === discipline.id,
  )
  if (fonction)
    fonction = { ...fonction, dateFin: discipline.endInfo?.date }
  emit('itemClic', fonction)
}
</script>

<template>
  <div v-if="filteredFilieres.length > 0" class="container">
    <v-card
      v-for="filiere in filteredFilieres"
      :key="filiere.codeFiliere"
      :subtitle="filiere.libelleFiliere"
      variant="tonal"
      min-height="100%"
    >
      <v-card-text>
        <div class="c-chip-group">
          <v-chip
            v-for="discipline in filiere.disciplines"
            :key="discipline.code"
            :text="discipline.disciplinePoste"
            :color="discipline.endInfo?.color ?? 'primary'"
            :ripple="false"
            rounded
            @="clickable ? { click: () => onClick(filiere, discipline) } : {}"
          >
            <template v-if="discipline.endInfo" #append>
              <v-tooltip
                :text="t(
                  discipline.endInfo.i18n,
                  {
                    date: format(discipline.endInfo.date!, 'P'),
                    months: discipline.endInfo.months,
                  },
                  discipline.endInfo.months && discipline.endInfo.months > 0 ? discipline.endInfo.months : 1,
                )"
                location="bottom start"
              >
                <template #activator="{ props: activatorProps }">
                  <v-icon v-bind="activatorProps" :icon="discipline.endInfo.icon" size="x-small" class="ms-1" />
                </template>
              </v-tooltip>
            </template>
          </v-chip>
        </div>
      </v-card-text>
    </v-card>
  </div>
  <slot v-else name="empty" />
</template>

<style scoped lang="scss">
.container {
  display: grid;
  gap: 0.75em;
  grid-template-columns: 1fr;

  @media (width >= 900px) {
    grid-template-columns: 1fr 1fr;
  }
}

.v-card-text {
  margin-top: -4px;
}

.c-chip-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
</style>
