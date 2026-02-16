import { mount } from '@vue/test-utils';
import { afterEach, describe, expect, it } from 'vitest';
import App from './App.vue';

describe('App', () => {
  afterEach(() => {
    window.history.pushState({}, '', '/');
  });

  it('renders guest login heading', () => {
    const wrapper = mount(App);

    expect(wrapper.get('h1').text()).toBe('Event Reservation MVP');
  });

  it('renders guest login controls', () => {
    const wrapper = mount(App);

    expect(wrapper.text()).toContain('ゲストでログイン');
    expect(wrapper.text()).toContain('セッション一覧');
    expect(wrapper.text()).toContain('セッション一覧を取得');
    expect(wrapper.text()).toContain('予約一覧を取得');
    expect(wrapper.text()).toContain('キーノートを予約');
    expect(wrapper.text()).toContain('マイページ');
    expect(wrapper.text()).toContain('マイページはログイン中ユーザーのみ表示できます。');
    expect(wrapper.text()).toContain('運営チェックイン');
    expect(wrapper.text()).toContain('運営チェックインはログイン中ユーザーのみ実行できます。');
  });

  it('hides admin controls on /participant route', () => {
    window.history.pushState({}, '', '/participant');
    const wrapper = mount(App);

    expect(wrapper.text()).toContain('Tokyo Product Summit 2026');
    expect(wrapper.text()).toContain('セッション一覧');
    expect(wrapper.text()).toContain('予約一覧');
    expect(wrapper.text()).toContain('マイページ');
    expect(wrapper.text()).not.toContain('セッション管理（運営）');
    expect(wrapper.text()).not.toContain('運営チェックイン');
  });
});
