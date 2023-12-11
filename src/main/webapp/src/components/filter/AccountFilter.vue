<script setup lang="ts">
import ChipsFilter from '@/components/filter/ChipsFilter.vue';
import { CategoriePersonne } from '@/types/enums/CategoriePersonne.ts';
import { Etat } from '@/types/enums/Etat.ts';
import type { SimplePersonne } from '@/types/personneType.ts';
import isEmpty from 'lodash.isempty';
import { ref, watch } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();

const props = defineProps<{
  searchList: Array<SimplePersonne> | undefined;
}>();

const emit = defineEmits<(event: 'update:result', payload: Array<SimplePersonne>) => void>();

const nbResults = ref<number>(props.searchList ? props.searchList.length : 0);

const filters = ref<{
  search: string | undefined;
  categories: Array<string>;
  status: Array<string>;
  types: Array<string>;
}>({
  search: undefined,
  categories: [],
  status: [],
  types: [],
});

const tags = {
  category: [
    { id: CategoriePersonne.Enseignant, i18n: 'person.category.teacher' },
    { id: CategoriePersonne.Non_enseignant_etablissement, i18n: 'person.category.nonTeacherSchool' },
    { id: CategoriePersonne.Non_enseignant_collectivite_locale, i18n: 'person.category.nonTeacherLocalCommunity' },
    { id: CategoriePersonne.Eleve, i18n: 'person.category.student' },
    { id: CategoriePersonne.Personne_relation_eleve, i18n: 'person.category.personRelationshipStudent' },
  ],
  status: [
    { id: Etat.Invalide, i18n: 'person.status.invalid' },
    { id: Etat.Valide, i18n: 'person.status.valid' },
    { id: Etat.Bloque, i18n: 'person.status.locked' },
    { id: Etat.Delete, i18n: 'person.status.deleted' },
    { id: Etat.Deleting, i18n: 'person.status.deleting' },
    { id: Etat.Incertain, i18n: 'person.status.uncertain' },
  ],
  type: [
    { id: '', i18n: 'source.official' },
    { id: 'SarapisUi_', i18n: 'source.SarapisUi_' },
  ],
};

const filter = () => {
  let result: Array<SimplePersonne> = !isEmpty(props.searchList) ? props.searchList! : [];

  const searchValue = filters.value.search ? filters.value.search.toLowerCase() : undefined;
  if (searchValue != undefined && searchValue !== '') {
    result = result.filter((personne) => {
      let filter = personne.cn.toLowerCase().indexOf(searchValue) > -1;
      if (personne.uid) filter = filter || personne.uid.toLowerCase().indexOf(searchValue) > -1;

      return filter;
    });
  }

  if (!isEmpty(filters.value.categories)) {
    result = result.filter((personne) => filters.value.categories.includes(personne.categorie));
  }

  if (!isEmpty(filters.value.status)) {
    result = result.filter((personne) => filters.value.status.includes(personne.etat));
  }

  if (!isEmpty(filters.value.types)) {
    result = result.filter((personne) =>
      filters.value.types.includes(personne.source.startsWith('SarapisUi_') ? 'SarapisUi_' : ''),
    );
  }

  nbResults.value = result.length;
  emit('update:result', result);
};

watch(
  () => props.searchList,
  (newValue, oldValue) => {
    if (newValue != oldValue) filter();
  },
);

watch(filters, () => filter(), { deep: true });
</script>

<template>
  <v-card flat>
    <v-card-text>
      <v-text-field
        v-model="filters.search"
        :placeholder="t('search.personne.placeholder')"
        variant="solo-filled"
        class="mb-3"
        rounded
        clearable
        flat
        hide-details
      >
        <template #prepend-inner>
          <v-icon icon="fas fa-search" size="x-small" class="mx-1" />
        </template>
      </v-text-field>
      <div class="mb-2">
        <h2 class="text-h6">{{ t('person.information.profile') }}</h2>
        <chips-filter v-model="filters.categories" :items="tags.category" />
      </div>
      <div class="mb-2">
        <h2 class="text-h6">{{ t('person.information.status') }}</h2>
        <chips-filter v-model="filters.status" :items="tags.status" />
      </div>
      <div class="mb-2">
        <h2 class="text-h6">{{ t('person.information.source') }}</h2>
        <chips-filter v-model="filters.types" :items="tags.type" />
      </div>
      <div class="d-flex">
        <v-spacer />
        <div>{{ nbResults }} {{ t('result', nbResults) }}</div>
      </div>
    </v-card-text>
  </v-card>
</template>
