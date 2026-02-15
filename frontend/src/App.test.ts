import { mount } from '@vue/test-utils';
import { describe, expect, it } from 'vitest';
import App from './App.vue';

describe('App', () => {
  it('renders startup heading', () => {
    const wrapper = mount(App);

    expect(wrapper.get('h1').text()).toBe('Frontend is up');
  });

  it('renders startup description', () => {
    const wrapper = mount(App);

    expect(wrapper.text()).toContain('Startup check sample page.');
  });
});
