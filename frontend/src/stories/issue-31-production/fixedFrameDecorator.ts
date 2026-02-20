import type { Decorator } from '@storybook/vue3-vite';

export const fixedFrameDecorator = (width: string, minHeight: string): Decorator => {
  return (story) => ({
    components: { story },
    template: `<div style="width:${width};min-height:${minHeight};margin:0 auto;padding:12px;background:var(--semantic-color-bg-canvas);"><story /></div>`,
  });
};
