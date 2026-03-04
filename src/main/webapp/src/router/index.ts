/**
 * Copyright (C) 2023 GIP-RECIA, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { createRouter, createWebHistory } from 'vue-router'

const isDev = import.meta.env.DEV

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...(
      isDev
        ? [
            {
              path: '/',
              name: 'index',
              component: () => import('@/views/IndexView.vue'),
            },
            {
              path: '/access',
              name: 'access',
              component: () => import('@/views/AccessView.vue'),
            },
            {
              path: '/admin',
              name: 'admin',
              component: () => import('@/views/AdminView.vue'),
            },
            {
              path: '/settings',
              name: 'settings',
              component: () => import('@/views/SettingsView.vue'),
            },
          ]
        : [{
            path: '/',
            redirect: () => {
              return { name: 'account' }
            },
          }]
    ),
    {
      path: '/account',
      children: [
        {
          path: '',
          name: 'account',
          components: {
            default: () => import('@/views/AccountView.vue'),
            header: () => import('@/components/tabbar/TabBar.vue'),
          },
        },
        {
          path: 'etab/:structureId(\\d+)',
          name: 'structure',
          components: {
            default: () => import('@/views/StructureView.vue'),
            header: () => import('@/components/tabbar/TabBar.vue'),
          },
        },
        {
          path: 'user/:userId(\\d+)',
          name: 'user',
          components: {
            default: () => import('@/views/UserView.vue'),
            header: () => import('@/components/tabbar/TabBar.vue'),
          },
        },
        {
          path: ':pathName(.*)',
          redirect: () => {
            return { name: 'account' }
          },
        },
      ],
    },
    {
      path: '/:pathName(.*)',
      redirect: () => {
        return { name: isDev ? 'index' : 'account' }
      },
    },
  ],
})

export default router
