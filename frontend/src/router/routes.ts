import type { RouteRecordRaw } from 'vue-router';
import { hasAdminSession } from '../composables/useReservationApp';
import AdminAuthView from '../views/AdminAuthView.vue';
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
    beforeEnter: () => {
      if (!hasAdminSession()) {
        return '/admin/auth';
      }
      return true;
    },
  },
  {
    path: '/admin/auth',
    name: 'admin-auth',
    component: AdminAuthView,
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
