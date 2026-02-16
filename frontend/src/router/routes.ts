import type { RouteRecordRaw } from 'vue-router';
import AdminView from '../views/AdminView.vue';
import ParticipantView from '../views/ParticipantView.vue';

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    redirect: '/participant',
  },
  {
    path: '/participant',
    name: 'participant',
    component: ParticipantView,
  },
  {
    path: '/admin',
    name: 'admin',
    component: AdminView,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/participant',
  },
];
