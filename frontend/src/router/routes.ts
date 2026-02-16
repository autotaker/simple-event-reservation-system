import type { RouteRecordRaw } from 'vue-router';
import OperatorView from '../views/OperatorView.vue';
import ParticipantView from '../views/ParticipantView.vue';

export const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'operator',
    component: OperatorView,
  },
  {
    path: '/participant',
    name: 'participant',
    component: ParticipantView,
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
];
