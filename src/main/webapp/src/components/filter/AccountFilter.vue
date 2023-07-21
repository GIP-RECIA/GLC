<script setup lang="ts">
import ChipsFilter from "@/components/filter/ChipsFilter.vue";
import { usePersonneStore } from "@/stores/personneStore";
import { CategoriePersonne } from "@/types/enums/CategoriePersonne";
import { Etat } from "@/types/enums/Etat";
import type { SearchPersonne } from "@/types/personneType";
import isEmpty from "lodash.isempty";
import { storeToRefs } from "pinia";
import { ref } from "vue";
import { useI18n } from "vue-i18n";

const emit =
  defineEmits<
    (event: "update:result", payload: Array<SearchPersonne>) => void
  >();

const { t } = useI18n();

const personneStore = usePersonneStore();
const { searchList } = storeToRefs(personneStore);

const nbResults = ref<number>(searchList.value ? searchList.value.length : 0);

let searchFilter: string | undefined;
let categoryFilter: Array<string>;
let statusFilter: Array<string>;

const setSearchFilter = (newValue: string | undefined) => {
  searchFilter = newValue;
  filter();
};

const setCategoryFilter = (newValue: Array<string>) => {
  categoryFilter = newValue;
  filter();
};

const setStatusFilter = (newValue: Array<string>) => {
  statusFilter = newValue;
  filter();
};

const filter = () => {
  const searchValue = searchFilter ? searchFilter.toLowerCase() : undefined;
  let result: Array<SearchPersonne> = !isEmpty(searchList.value)
    ? searchList.value!
    : [];

  if (searchValue != undefined && searchValue !== "") {
    result = result.filter((personne) => {
      let filter = personne.displayName.toLowerCase().indexOf(searchValue) > -1;

      if (personne.uid) {
        filter = filter || personne.uid.toLowerCase().indexOf(searchValue) > -1;
      }

      return filter;
    });
  }

  if (!isEmpty(categoryFilter)) {
    result = result?.filter((personne) =>
      categoryFilter.includes(personne.categorie)
    );
  }

  if (!isEmpty(statusFilter)) {
    result = result?.filter((personne) => statusFilter.includes(personne.etat));
  }

  nbResults.value = result.length;
  emit("update:result", result);
};

const categoryTags = [
  // { "id": CategoriePersonne., i18n: "admin" },
  { id: CategoriePersonne.Enseignant, i18n: "teacher" },
  { id: CategoriePersonne.Eleve, i18n: "student" },
  {
    id: CategoriePersonne.Personne_relation_eleve,
    i18n: "legalRepresentative",
  },
  {
    id: CategoriePersonne.Non_enseignant_collectivite_locale,
    i18n: "communityStaff",
  },
  {
    id: CategoriePersonne.Non_enseignant_etablissement,
    i18n: "structureStaff",
  },
];

const statusTags = [
  { id: Etat.Invalide, i18n: "invalid" },
  { id: Etat.Valide, i18n: "valid" },
  { id: Etat.Bloque, i18n: "locked" },
  { id: Etat.Delete, i18n: "deleted" },
];
</script>

<template>
  <v-card flat>
    <v-card-text>
      <v-text-field
        variant="solo-filled"
        rounded
        clearable
        flat
        hide-details
        @update:model-value="setSearchFilter"
      />
    </v-card-text>
    <v-card-text>
      <h2 class="text-h6 mb-2">{{ t("category") }}</h2>
      <chips-filter :tags="categoryTags" @update:selected="setCategoryFilter" />
    </v-card-text>
    <v-card-text>
      <h2 class="text-h6 mb-2">{{ t("status") }}</h2>
      <chips-filter :tags="statusTags" @update:selected="setStatusFilter" />
    </v-card-text>
    <v-card-text> {{ nbResults }} {{ t("result", nbResults) }} </v-card-text>
  </v-card>
</template>
