import type { StorybookConfig } from '@storybook/vue3-vite';
import { mergeConfig } from 'vite';

const config: StorybookConfig = {
  stories: ['../src/**/*.mdx', '../src/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: ['@storybook/addon-a11y', '@storybook/addon-docs'],
  framework: '@storybook/vue3-vite',
  async viteFinal(config) {
    return mergeConfig(config, {
      base: './',
      optimizeDeps: {
        include: ['react', 'react-dom/client', 'react/jsx-runtime'],
      },
    });
  },
};

export default config;
