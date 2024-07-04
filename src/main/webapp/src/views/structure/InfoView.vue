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
import { useStructureStore } from '@/stores/structureStore.ts';
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
