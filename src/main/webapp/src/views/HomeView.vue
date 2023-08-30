<script setup lang="ts">
import CustomPagination from "@/components/CustomPagination.vue";
import { useStructureStore } from "@/stores/structureStore";
import type { SimpleEtablissement } from "@/types/etablissementType";
import { storeToRefs } from "pinia";
import { watch, ref } from "vue";
import { useI18n } from "vue-i18n";

const { t } = useI18n();

const structureStore = useStructureStore();
structureStore.init();
const { etabs } = storeToRefs(structureStore);

const select = ref<string>();
const items = ref<Array<SimpleEtablissement> | undefined>();
const pageItems = ref<Array<SimpleEtablissement> | undefined>();

watch(etabs, (newValue) => {
  if (typeof newValue !== "undefined" && newValue !== null)
    items.value = newValue;
});

watch(select, (newValue) => {
  if (typeof newValue !== "undefined" && newValue !== null) {
    const searchValue = newValue
      .toLowerCase()
      .normalize("NFD")
      .replace(/\p{Diacritic}/gu, "");
    items.value = etabs.value?.filter((etablissement) => {
      let filters =
        etablissement.nom.toLowerCase().indexOf(searchValue) > -1 ||
        etablissement.uai.toLowerCase().indexOf(searchValue) > -1 ||
        etablissement.siren.toString().indexOf(searchValue) > -1;
      if (etablissement.ville) {
        filters =
          filters ||
          etablissement.ville.toLowerCase().indexOf(searchValue) > -1;
      }

      return filters;
    });
  } else items.value = etabs.value;
});
</script>

<template>
  <v-container>
    <v-text-field
      v-model="select"
      :placeholder="t('search.structure.placeholder')"
      variant="solo"
      rounded
      clearable
      flat
      hide-details
      class="mb-8"
    >
      <template #prepend-inner>
        <v-icon icon="fas fa-search" size="x-small" class="mx-1" />
      </template>
    </v-text-field>
    <v-row>
      <v-col
        v-for="(etablissement, index) in pageItems"
        :key="index"
        :cols="12"
        :md="6"
        :lg="4"
        :xl="3"
        class="pa-2"
      >
        <v-card
          :to="{
            name: 'structure',
            params: { structureId: etablissement.id },
          }"
          class="h-100"
          flat
        >
          <v-card-text class="card-info">
            <div class="title">
              {{ etablissement.nom }}
            </div>
            <div class="subtitle">
              {{
                etablissement.type
                  ? `${etablissement.type} ${etablissement.uai}`
                  : etablissement.uai
              }}
            </div>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
    <custom-pagination
      :items="items"
      :items-per-page="20"
      hide-single-page
      class="mt-8"
      @update:page="(items) => (pageItems = items)"
    />
  </v-container>
</template>

<style scoped lang="scss">
.card-info {
  display: block;
  flex: none;
  letter-spacing: 0.0178571429em;
  overflow: hidden;
  text-overflow: ellipsis;
  text-transform: none;
  white-space: nowrap;

  .subtitle {
    font-size: 0.775rem;
    font-weight: 400;
    opacity: var(--v-medium-emphasis-opacity);
  }
}
</style>
