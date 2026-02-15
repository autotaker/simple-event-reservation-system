import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import App from './App.vue';

describe('App', () => {
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
  });
});
