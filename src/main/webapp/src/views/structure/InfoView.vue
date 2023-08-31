<script setup lang="ts">
import { useStructureStore } from '@/stores/structureStore';
import { storeToRefs } from 'pinia';

const structureStore = useStructureStore();
const { currentEtab } = storeToRefs(structureStore);
</script>

<template>
  <v-container fluid>
    <v-card flat>
      <v-card-text v-if="currentEtab">
        <div>uai : {{ currentEtab.uai }}</div>
        <div>etat : {{ currentEtab.etat }}</div>
        <div>etatAlim : {{ currentEtab.etatAlim }}</div>
        <div>source : {{ currentEtab.source }}</div>
        <div>anneeScolaire : {{ currentEtab.anneeScolaire }}</div>
        <div>adresse : {{ currentEtab.adresse }}</div>
        <div>categorie : {{ currentEtab.categorie }}</div>
        <div>mail : {{ currentEtab.mail }}</div>
        <div>nom : {{ currentEtab.nom }}</div>
        <div>nomCourt : {{ currentEtab.nomCourt }}</div>
        <div>siren : {{ currentEtab.siren }}</div>
        <div>siteWeb : {{ currentEtab.siteWeb }}</div>
        <div>modeleLogin : {{ currentEtab.modeleLogin }}</div>
        <div>logo : {{ currentEtab.logo }}</div>
      </v-card-text>
    </v-card>
  </v-container>
</template>
