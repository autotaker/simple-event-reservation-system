import type { Preview } from '@storybook/vue3-vite';
import '../src/styles/base.css';
import '../src/styles/layout.css';
import '../src/styles/components.css';
import '../src/styles/state.css';
import '../src/styles/tokens.css';

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },

    a11y: {
      // 'todo' - show a11y violations in the test UI only
      // 'error' - fail CI on a11y violations
      // 'off' - skip a11y checks entirely
      test: 'todo',
    },
  },
};

export default preview;
