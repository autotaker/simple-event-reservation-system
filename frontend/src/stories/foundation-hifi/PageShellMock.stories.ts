import type { Meta, StoryObj } from '@storybook/vue3-vite';
import PageShellMock from './PageShellMock.vue';

const meta = {
  title: 'US-13 Foundation HiFi/PageShellMock',
  component: PageShellMock,
  tags: ['autodocs'],
} satisfies Meta<typeof PageShellMock>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MobileShell: Story = {
  args: { compact: true },
  render: (args) => ({
    components: { PageShellMock },
    setup: () => ({ args }),
    template: '<PageShellMock v-bind="args">Shell content</PageShellMock>',
  }),
  parameters: {
    docs: { description: { story: '想定デバイス: 参加者スマートフォン (390x844)' } },
  },
};

export const TabletShell: Story = {
  args: { compact: false },
  render: (args) => ({
    components: { PageShellMock },
    setup: () => ({ args }),
    template: '<PageShellMock v-bind="args">Shell content</PageShellMock>',
  }),
  parameters: {
    docs: { description: { story: '想定デバイス: 受付補助タブレット (834x1112)' } },
  },
};
