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
</script>

<template>
  <v-container>
    <h1>Je veux</h1>
    <ul>
      <li>
        <router-link :to="{ name: 'account' }">
          Gérer les comptes
        </router-link>
      </li>
      <li>
        <router-link :to="{ name: 'access' }">
          Gérer les accès aux services
        </router-link>
      </li>
      <li>
        <router-link :to="{ name: 'admin' }">
          Gérer les droits d'administration
        </router-link>
      </li>
      <li>
        <a href="/portail/api/ExternalURLStats?fname=I2Grouper-UI&service=/grouperUi/">
          Gérer les groupes
        </a>
      </li>
      <li>
        <router-link :to="{ name: 'settings' }">
          Paramétrer l'établissement
        </router-link>
      </li>
    </ul>
  </v-container>
</template>

<style scoped lang="scss">
</style>
