import type { RouteRecordRaw } from 'vue-router';
import AdminView from '../views/AdminView.vue';
import OperatorView from '../views/OperatorView.vue';
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
    path: '/operator',
    name: 'operator',
    component: OperatorView,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/participant',
  },
];
