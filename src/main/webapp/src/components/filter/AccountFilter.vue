<script setup lang="ts">
import ChipsFilter from '@/components/filter/ChipsFilter.vue';
import { CategoriePersonne } from '@/types/enums/CategoriePersonne';
import { Etat } from '@/types/enums/Etat';
import type { SimplePersonne } from '@/types/personneType';
import { isArrayOf } from '@/utils/arrayUtils';
import isEmpty from 'lodash.isempty';
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  searchList: Array<SimplePersonne> | undefined;
}>();

const emit = defineEmits<(event: 'update:result', payload: Array<SimplePersonne>) => void>();

const nbResults = ref<number>(props.searchList ? props.searchList.length : 0);

let searchFilter: string | undefined;
let categoryFilter: Array<string>;
let statusFilter: Array<string>;
let typeFilter: Array<string>;

const setSearchFilter = (newValue: string | undefined) => {
  searchFilter = newValue;
  filter();
};

const setCategoryFilter = (newValue: Array<number | string>) => {
  if (isArrayOf(newValue, 'string')) {
    categoryFilter = newValue as string[];
    filter();
  }
};

const setStatusFilter = (newValue: Array<number | string>) => {
  if (isArrayOf(newValue, 'string')) {
    statusFilter = newValue as string[];
    filter();
  }
};

const setTypeFilter = (newValue: Array<number | string>) => {
  if (isArrayOf(newValue, 'string')) {
    typeFilter = newValue as string[];
    filter();
  }
};

const filter = () => {
  const searchValue = searchFilter ? searchFilter.toLowerCase() : undefined;
  let result: Array<SimplePersonne> = !isEmpty(props.searchList) ? props.searchList! : [];

  if (searchValue != undefined && searchValue !== '') {
    result = result.filter((personne) => {
      let filter = personne.cn.toLowerCase().indexOf(searchValue) > -1;

      if (personne.uid) {
        filter = filter || personne.uid.toLowerCase().indexOf(searchValue) > -1;
      }

      return filter;
    });
  }

  if (!isEmpty(categoryFilter)) {
    result = result?.filter((personne) => categoryFilter.includes(personne.categorie));
  }

  if (!isEmpty(statusFilter)) {
    result = result?.filter((personne) => statusFilter.includes(personne.etat));
  }

  if (!isEmpty(typeFilter)) {
    result = result.filter((personne) =>
      typeFilter.includes(personne.source.startsWith('SarapisUi_') ? 'SarapisUi_' : ''),
    );
  }

  nbResults.value = result.length;
  emit('update:result', result);
};

const categoryTags = [
  // { "id": CategoriePersonne., i18n: "admin" },
  { id: CategoriePersonne.Enseignant, i18n: 'person.category.teacher' },
  { id: CategoriePersonne.Eleve, i18n: 'person.category.student' },
  {
    id: CategoriePersonne.Personne_relation_eleve,
    i18n: 'person.category.personRelationshipStudent',
  },
  {
    id: CategoriePersonne.Non_enseignant_collectivite_locale,
    i18n: 'person.category.nonTeacherLocalCommunity',
  },
  {
    id: CategoriePersonne.Non_enseignant_etablissement,
    i18n: 'person.category.nonTeacherSchool',
  },
];

const statusTags = [
  { id: Etat.Invalide, i18n: 'person.status.invalid' },
  { id: Etat.Valide, i18n: 'person.status.valid' },
  { id: Etat.Bloque, i18n: 'person.status.locked' },
  { id: Etat.Delete, i18n: 'person.status.deleted' },
];

const typeTags = [
  { id: '', i18n: 'source.official' },
  { id: 'SarapisUi_', i18n: 'source.SarapisUi_' },
];

filter();
</script>

<template>
  <v-card flat>
    <v-card-text>
      <v-text-field
        :placeholder="t('search.personne.placeholder')"
        variant="solo-filled"
        class="mb-3"
        rounded
        clearable
        flat
        hide-details
        @update:model-value="setSearchFilter"
      >
        <template #prepend-inner>
          <v-icon icon="fas fa-search" size="x-small" class="mx-1" />
        </template>
      </v-text-field>
      <div class="mb-2">
        <h2 class="text-h6">{{ t('person.information.profile') }}</h2>
        <chips-filter :tags="categoryTags" @update:selected="setCategoryFilter" />
      </div>
      <div class="mb-2">
        <h2 class="text-h6">{{ t('person.information.status') }}</h2>
        <chips-filter :tags="statusTags" @update:selected="setStatusFilter" />
      </div>
      <div class="mb-2">
        <h2 class="text-h6">{{ t('person.information.source') }}</h2>
        <chips-filter :tags="typeTags" @update:selected="setTypeFilter" />
      </div>
      <div class="d-flex">
        <v-spacer />
        <div>{{ nbResults }} {{ t('result', nbResults) }}</div>
      </div>
    </v-card-text>
  </v-card>
</template>
