import { mount } from '@vue/test-utils';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';
import { createMemoryHistory, createRouter } from 'vue-router';
import App from './App.vue';
import { routes } from './router/routes';

const mountAt = async (path: string) => {
  const router = createRouter({
    history: createMemoryHistory(),
    routes,
  });

  await router.push(path);
  await router.isReady();

  return mount(App, {
    global: {
      plugins: [router],
    },
  });
};

describe('App routing', () => {
  beforeEach(() => {
    window.localStorage.clear();
    window.sessionStorage.clear();
    vi.unstubAllGlobals();
  });

  afterEach(() => {
    window.localStorage.clear();
    window.sessionStorage.clear();
    vi.unstubAllGlobals();
  });

  it('redirects root route to participant flow', async () => {
    const wrapper = await mountAt('/');

    expect(wrapper.text()).toContain('Tokyo Product Summit 2026');
    expect(wrapper.text()).toContain('セッション一覧');
  });

  it('renders participant flow on /participant route', async () => {
    const wrapper = await mountAt('/participant');

    expect(wrapper.text()).toContain('Tokyo Product Summit 2026');
    expect(wrapper.text()).toContain('セッション一覧');
    expect(wrapper.text()).toContain('予約一覧');
    expect(wrapper.text()).toContain('マイページ');
  });

  it('does not render admin controls on /participant route', async () => {
    const wrapper = await mountAt('/participant');

    expect(wrapper.text()).not.toContain('セッション管理（運営）');
    expect(wrapper.text()).not.toContain('運営チェックイン');
  });

  it('does not render admin management screen on /admin without admin token', async () => {
    const wrapper = await mountAt('/admin');

    expect(wrapper.text()).toContain('管理権限がないため /admin の管理画面を表示できません。');
    expect(wrapper.text()).toContain('参加者画面へ戻る');
    expect(wrapper.text()).not.toContain('セッション管理（運営）');
  });

  it('does not restore admin access from localStorage on /admin route', async () => {
    window.localStorage.setItem('adminAccessToken', 'legacy-persisted-token');
    const wrapper = await mountAt('/admin');

    expect(wrapper.text()).toContain('管理権限がないため /admin の管理画面を表示できません。');
    expect(wrapper.text()).not.toContain('セッション管理（運営）');
  });

  it('restores admin access from sessionStorage on /admin route', async () => {
    window.sessionStorage.setItem('adminAccessToken', 'session-token');
    vi.stubGlobal(
      'fetch',
      vi.fn(async () => new Response(JSON.stringify({ sessions: [] }), { status: 200 })),
    );
    const wrapper = await mountAt('/admin');

    expect(wrapper.text()).toContain('セッション管理（運営）');
    expect(wrapper.text()).not.toContain('管理権限がないため /admin の管理画面を表示できません。');
  });

  it('keeps operator check-in flow reachable on /operator route', async () => {
    const wrapper = await mountAt('/operator');

    expect(wrapper.text()).toContain('運営チェックイン');
    expect(wrapper.text()).toContain('セッション一覧');
    expect(wrapper.text()).toContain('セッション管理（運営）');
  });
});
