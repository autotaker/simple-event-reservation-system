import { mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, it } from 'vitest';
import { createMemoryHistory, createRouter } from 'vue-router';
import { nextTick } from 'vue';
import App from './App.vue';
import { routes } from './router/routes';

const mountAt = async (path: string) => {
  const router = createRouter({
    history: createMemoryHistory(),
    routes,
  });

  await router.push(path);
  await router.isReady();

  const wrapper = mount(App, {
    global: {
      plugins: [router],
    },
  });
  await nextTick();
  await nextTick();
  return { wrapper, router };
};

describe('App routing', () => {
  beforeEach(() => {
    window.localStorage.clear();
    window.sessionStorage.clear();
  });

  afterEach(() => {
    window.localStorage.clear();
    window.sessionStorage.clear();
  });

  it('redirects root route to participant flow', async () => {
    const { wrapper } = await mountAt('/');

    expect(wrapper.text()).toContain('Tokyo Product Summit 2026');
    expect(wrapper.text()).toContain('セッション一覧');
  });

  it('renders participant flow on /participant route', async () => {
    const { wrapper } = await mountAt('/participant');

    expect(wrapper.text()).toContain('Tokyo Product Summit 2026');
    expect(wrapper.text()).toContain('セッション一覧');
    expect(wrapper.text()).toContain('予約一覧');
    expect(wrapper.text()).toContain('マイページ');
  });

  it('does not render admin controls on /participant route', async () => {
    const { wrapper } = await mountAt('/participant');

    expect(wrapper.text()).not.toContain('セッション管理（運営）');
    expect(wrapper.text()).not.toContain('運営チェックイン');
  });

  it('redirects /admin to /admin/auth without admin token', async () => {
    const { wrapper, router } = await mountAt('/admin');

    expect(router.currentRoute.value.path).toBe('/admin/auth');
    expect(wrapper.text()).toContain('管理者ログイン');
    expect(wrapper.text()).toContain('運用者ID（operatorId）');
    expect(wrapper.text()).not.toContain('セッション管理（運営）');
  });

  it('keeps operator check-in flow reachable on /operator route', async () => {
    const { wrapper } = await mountAt('/operator');

    expect(wrapper.text()).toContain('運営チェックイン');
    expect(wrapper.text()).toContain('セッション一覧');
    expect(wrapper.text()).toContain('セッション管理（運営）');
  });
});
