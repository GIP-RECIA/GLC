import { createRouter, createWebHistory } from 'vue-router';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/:structureId(\\d+)',
      name: 'structure',
      component: () => import('@/views/StructureView.vue'),
    },
    {
      path: '/:pathName(.*)',
      redirect: () => {
        return { name: 'home' };
      },
    },
  ],
});

export default router;
