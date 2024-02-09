<script setup lang="ts">
import { useConfigurationStore } from '@/stores/configurationStore.ts';
import type { Filiere } from '@/types/filiereType.ts';
import type { PersonneFonction } from '@/types/fonctionType.ts';
import { getDateFin } from '@/utils/accountUtils.ts';
import { format } from 'date-fns';
import { storeToRefs } from 'pinia';
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const configurationStore = useConfigurationStore();
const { currentStructureId } = storeToRefs(configurationStore);

const { t } = useI18n();

const props = defineProps<{
  filieres: Array<Filiere> | undefined;
  fonctions: Array<PersonneFonction> | undefined;
}>();

const filteredFilieres = ref<Array<Filiere>>([]);

const filterFilieres = (): void => {
  if (props.filieres && props.fonctions) {
    const etabFonctions = props.fonctions.filter((fonction) => fonction.structure == currentStructureId.value);
    const filiereIds = [...new Set(etabFonctions.map((fonction) => fonction.filiere))];
    const disciplineIds = [...new Set(etabFonctions.map((fonction) => fonction.disciplinePoste))];
    const datesFin = etabFonctions.map((fonction) => {
      return { disciplinePoste: fonction.disciplinePoste, dateFin: fonction.dateFin };
    });

    filteredFilieres.value = props.filieres
      .filter((filiere) => filiereIds.includes(filiere.id))
      .map((filiere) => {
        let disciplines = filiere.disciplines.filter((discipline) => disciplineIds.includes(discipline.id));
        disciplines = disciplines.map((discipline) => {
          const dateFin = datesFin.find((endDate) => endDate.disciplinePoste == discipline.id)?.dateFin;

          return { ...discipline, endInfo: dateFin ? getDateFin(dateFin) : undefined };
        });

        return { ...filiere, disciplines };
      });
  }
};

watch(
  () => props.fonctions,
  () => filterFilieres(),
  { immediate: true },
);
</script>

<template>
  <v-row v-if="filteredFilieres.length > 0">
    <v-col v-for="(filiere, index) in filteredFilieres" :key="index" :cols="12" :md="6" class="pa-2">
      <v-card :subtitle="filiere.libelleFiliere" variant="tonal" min-height="100%">
        <v-card-text class="pb-2 mt--3">
          <div class="v-chip-group v-chip-group--column flex-wrap">
            <v-chip
              v-for="(discipline, index) in filiere.disciplines"
              :key="index"
              :text="discipline.disciplinePoste"
              :color="discipline.endInfo?.color ?? 'primary'"
              :ripple="false"
              rounded
            >
              <template #append v-if="discipline.endInfo">
                <v-tooltip
                  :text="
                    t(
                      discipline.endInfo.i18n,
                      {
                        date: format(discipline.endInfo.date!, 'P'),
                        months: discipline.endInfo.months,
                      },
                      discipline.endInfo.months ?? 1,
                    )
                  "
                  location="bottom start"
                >
                  <template v-slot:activator="{ props }">
                    <v-icon v-bind="props" :icon="discipline.endInfo.icon" size="x-small" class="ml-1" />
                  </template>
                </v-tooltip>
              </template>
            </v-chip>
          </div>
        </v-card-text>
      </v-card>
    </v-col>
  </v-row>
  <div v-else>
    <slot name="empty" />
  </div>
</template>
